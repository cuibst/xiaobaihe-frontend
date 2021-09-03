package com.java.cuiyikai.entities;

public class BottomFavouriteEntity {

    private final boolean favoured;

    private final String directoryName;

    public BottomFavouriteEntity(boolean favoured, String directoryName) {
        this.favoured = favoured;
        this.directoryName = directoryName;
    }

    public boolean isFavoured() {
        return favoured;
    }

    public String getDirectoryName() {
        return directoryName;
    }

}
