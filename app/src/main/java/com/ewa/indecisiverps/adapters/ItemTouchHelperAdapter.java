package com.ewa.indecisiverps.adapters;

/**
 * Created by ewa on 12/20/2016.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
