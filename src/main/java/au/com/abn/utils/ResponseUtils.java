package au.com.abn.utils;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseUtils {

    private static final String ENCODING = "UTF-8";

    private static final Logger LOG = LogManager.getLogger(ResponseUtils.class);

    public static void sendFileToUser(final File file, final HttpServletResponse response, HttpStatus status) throws IOException {
        if (file != null) {
            String filename = file.getName();
            try (OutputStream out = response.getOutputStream()) {

                Path path = file.toPath();

                response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
                response.setContentType(Files.probeContentType(path));
                response.setContentLength(Long.valueOf(file.length()).intValue());
                response.setStatus(status.value());

                Files.copy(path, out);
                out.flush();

            }
            FileUtils.deleteQuietly(file);
        } else {
            try (PrintWriter out = response.getWriter()) {
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.flush();
                LOG.info("Empty file found. Sending an empty response.");
            }
        }
    }

    public static void sendResponseToUser(final Object object, final HttpServletResponse response, HttpStatus status) {
        try {
            String output = new Gson().toJson(object);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status.value());
            response.setContentLength(output.getBytes(ENCODING).length);
            IOUtils.write(output, response.getOutputStream(), StandardCharsets.UTF_8);
            LOG.info("Response sent successfully to user: %s".formatted(output));
        } catch (final IOException e) {
            LOG.error("IOException occurred: %s".formatted(e.getMessage()), e);
        }
    }

    public static void sendResponseToUser(final String output, final HttpServletResponse response, HttpStatus status) {
        try {

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status.value());
            response.setContentLength(output.getBytes(ENCODING).length);
            IOUtils.write(output, response.getOutputStream(), StandardCharsets.UTF_8);
            LOG.info("Response sent successfully to user: %s".formatted(output));
        } catch (final IOException e) {
            LOG.error("IOException occurred: %s".formatted(e.getMessage()), e);
        }
    }

    public static void sendResponseToUser(HttpServletResponse response, HttpStatus status) {
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(status.value());

            out.flush();
        } catch (final IOException e) {
            LOG.error("IOException occurred: %s".formatted(e.getMessage()), e);
        }
    }

}