package com.example.whatsapp_android_clone;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabsAdapter extends FragmentStateAdapter {


    public TabsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1:
                Fragment statusFragment = new StatusFragment();
                return statusFragment;

            case 2:
                Fragment callsFragment = new CallsFragment();
                return callsFragment;
        }

        Fragment chatsFragment = new ChatsFragment();
        return chatsFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
