package com.hjsoft.guestbooktaxi.model;

/**
 * Created by hjsoft on 17/12/16.
 */
public class CabDist {

    long dist;
    int position;

    public CabDist(long dist,int position)
    {
        this.dist=dist;
        this.position=position;
    }

    public long getDist() {
        return dist;
    }

    public void setDist(long dist) {
        this.dist = dist;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
