package ditzdevs.pixelify.me.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ditzdevs.pixelify.me.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {
    
   private ActivityMainBinding binding;  
       
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       binding = ActivityMainBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
   }
}