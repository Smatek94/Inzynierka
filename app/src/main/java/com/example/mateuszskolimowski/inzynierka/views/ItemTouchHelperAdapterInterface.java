package com.example.mateuszskolimowski.inzynierka.views;

/**
 * Created by Mateusz Skolimowski on 14.04.2017.
 */

public interface ItemTouchHelperAdapterInterface {
    void onItemMove(int fromPosition, int toPosition);

    void updateRoute();
}
