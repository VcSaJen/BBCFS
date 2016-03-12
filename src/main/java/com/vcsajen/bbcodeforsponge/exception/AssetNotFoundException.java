package com.vcsajen.bbcodeforsponge.exception;

/**
 * Created by VcSaJen on 11.03.2016 20:32.
 */
public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException() {
        super("Some asset not found!");
    }

    public AssetNotFoundException(String message) {
        super(message);
    }

    public AssetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetNotFoundException(Throwable cause) {
        super(cause);
    }
}
