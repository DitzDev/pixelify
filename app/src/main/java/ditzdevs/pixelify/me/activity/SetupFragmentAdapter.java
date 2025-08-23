package ditzdevs.pixelify.me.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SetupFragmentAdapter extends FragmentStateAdapter {
    
    public SetupFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new AdbFragment();
            case 1 -> new ShizukuFragment();
            default -> new AdbFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }
    
    public String getTabTitle(int position) {
        return switch (position) {
            case 0 -> "ADB";
            case 1 -> "Shizuku";
            default -> "ADB";
        };
    }
}