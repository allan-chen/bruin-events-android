package allanchen.com.ucla_events;

import android.support.annotation.NonNull;

/**
 * Created by Allan on 9/15/2016.
 */
class Category implements Comparable {
    public String name;
    public int resId;

    public Category(String name,int resId){
        this.name = name;
        this.resId = resId;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        Category other = (Category) another;
        return this.name.compareTo(other.name);
    }
}
