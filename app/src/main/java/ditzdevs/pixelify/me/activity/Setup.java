package ditzdevs.pixelify.me.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayoutMediator;
import ditzdevs.pixelify.me.R;
import ditzdevs.pixelify.me.databinding.LayoutSetupBinding;
import ditzdevs.pixelify.me.utils.ComponentUtils;
import ditzdevs.pixelify.me.utils.PermissionsHelper;

public class Setup extends AppCompatActivity {
    private LayoutSetupBinding binding;
    private ComponentUtils cpmt = new ComponentUtils();
    private SetupFragmentAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
   
        if (PermissionsHelper.hasWriteSecureSettings(this)) {
            navigateToMain();
            return;
        }
        
        setupViewPager();
    }
    
    private void setupViewPager() {
        adapter = new SetupFragmentAdapter(this);
        binding.viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
            (tab, position) -> tab.setText(adapter.getTabTitle(position))
        ).attach();
    }
    
    private void navigateToMain() {
        startActivity(new Intent(this, Main.class));
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        if (PermissionsHelper.hasWriteSecureSettings(this)) {
            navigateToMain();
        }
    }
     
    @Override
    public void onBackPressed() {
        cpmt.showDialogPosNeg(this, "Warning", getString(R.string.q_onbackpressed_setup), 
            this::finish, () -> {});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}