package org.ngs.objectstorage.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Slf4j
@Service
public class DiskService {

    @Value("${object.storage.root-folder}")
    private String rootFolder;

    public MessageDigest createMd5() {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("md4 not found");
        }
        return md5;
    }

    public void persistToDisk(String fileUUID, String md5Hash, long fileSize, InputStream inputStream) {
        Path target = Path.of(rootFolder, fileUUID);
        MessageDigest md5 = createMd5();
        long bytesWritten = 0;
        try (DigestInputStream dis = new DigestInputStream(inputStream, md5);
             OutputStream out = Files.newOutputStream(target, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = dis.read(buffer)) != -1) {
                bytesWritten += read;
                if (bytesWritten > fileSize) {
                    throw new RuntimeException("upload exceeds size allocated for file " + fileUUID);
                }
                out.write(buffer, 0, read);
            }
        } catch (Exception e) {
            deleteFileIfExists(target);
            throw new RuntimeException("failed upload " + fileUUID, e);
        }

        if (bytesWritten != fileSize) {
            deleteFileIfExists(target);
            throw new RuntimeException("payload size mismatch " + fileUUID);
        }
        String fileMd5 = HexFormat.of().formatHex(md5.digest());
        log.info("fileMd5 {} fileUUID {}", fileMd5, fileUUID);
        if (!md5Hash.equals(fileMd5)) {
            throw new RuntimeException("md5 hash did not match");
        }
    }

    private static boolean deleteFileIfExists(Path target) {
        try {
            Files.deleteIfExists(target);
            return true;
        } catch (Exception e) {
            log.error("unable to delete file ", e);
            return false;
        }
    }

    public void writeFileToServletStream(String fileUUID, HttpServletResponse httpServletResponse) {
        Path target = Path.of(rootFolder, fileUUID);
        try (InputStream in = Files.newInputStream(target);
             OutputStream out = httpServletResponse.getOutputStream()
        ) {
            in.transferTo(out);
        } catch (Exception e) {
            throw new RuntimeException("failed to file to output stream " + e);
        }
    }

    public boolean deleteFile(String fileUUID) {
        Path target = Path.of(rootFolder, fileUUID);
        if (!Files.exists(target)) {
            return true;
        }
        return deleteFileIfExists(target);
    }
}
