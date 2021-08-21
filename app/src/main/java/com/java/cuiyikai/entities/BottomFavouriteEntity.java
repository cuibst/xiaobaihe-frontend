package com.java.cuiyikai.entities;

public class BottomFavouriteEntity {

    private boolean favoured;

    private String directoryName;

    public BottomFavouriteEntity(boolean favoured, String directoryName) {
        this.favoured = favoured;
        this.directoryName = directoryName;
    }

    public boolean isFavoured() {
        return favoured;
    }

    public void setFavoured(boolean favoured) {
        this.favoured = favoured;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
