package com.goodformentertainment.bc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.canarymod.plugin.Plugin;

public final class JarUtil {
    public static boolean exportResource(final Plugin plugin, final String resourceName,
            final File targetDir) throws IOException {
        boolean success = false;

        final File targetFile = new File(targetDir, resourceName);
        if (!targetFile.exists()) {
            final InputStream in = plugin.getClass().getResourceAsStream("/" + resourceName);
            OutputStream out = null;
            if (in != null) {
                try {
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }

                    if (targetFile.createNewFile()) {
                        int readBytes;
                        final byte[] buffer = new byte[1024];
                        out = new FileOutputStream(targetFile);
                        while ((readBytes = in.read(buffer)) > 0) {
                            out.write(buffer, 0, readBytes);
                        }

                        plugin.getLogman().info("Wrote default " + resourceName);
                        success = true;
                    }
                } finally {
                    in.close();
                    if (out != null) {
                        out.close();
                    }
                }
            }
        } else {
            plugin.getLogman().info("Using existing " + resourceName);
        }

        return success;
    }

    public static boolean exportResource(final Plugin plugin, final String resourceName,
            final File targetDir, final String targetName) throws IOException {
        boolean success = false;

        final File targetFile = new File(targetDir, targetName);
        if (!targetFile.exists()) {
            final InputStream in = plugin.getClass().getResourceAsStream("/" + resourceName);
            OutputStream out = null;
            if (in != null) {
                try {
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }

                    if (targetFile.createNewFile()) {
                        int readBytes;
                        final byte[] buffer = new byte[1024];
                        out = new FileOutputStream(targetFile);
                        while ((readBytes = in.read(buffer)) > 0) {
                            out.write(buffer, 0, readBytes);
                        }

                        plugin.getLogman().info("Wrote default " + targetName);
                        success = true;
                    }
                } finally {
                    in.close();
                    if (out != null) {
                        out.close();
                    }
                }
            }
        } else {
            plugin.getLogman().info("Using existing " + resourceName);
        }

        return success;
    }
}
