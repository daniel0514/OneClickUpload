package com.oneclickupload.danielhsiao.oneclickupload;

import android.graphics.drawable.Drawable;

/**
 * Created by Daniel Hsiao on 2017-06-19.
 */

public class ItemAccount {
    private boolean checked;
    private Drawable itemDrawable;
    private String itemString;

    public ItemAccount(boolean checked, Drawable itemDrawable, String itemString){
        this.checked = checked;
        this.itemDrawable = itemDrawable;
        this.itemString = itemString;
    }

    public boolean isChecked(){return checked;}
    public void setChecked(boolean isChecked){this.checked = isChecked;}
    public String getString(){return itemString;}
    public Drawable getDrawable(){return itemDrawable;}
}
