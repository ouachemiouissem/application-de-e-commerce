package com.aliwis.bytebye.ui.vendor;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliwis.bytebye.R;
import com.aliwis.bytebye.databinding.DialogNotificationBinding;
import com.aliwis.bytebye.databinding.FragmentAdminHomeBinding;
import com.aliwis.bytebye.notification.FCMSend;
import com.aliwis.bytebye.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class AdminHomeFragment extends Fragment {


    FragmentAdminHomeBinding binding;
    DialogNotificationBinding notificationDialogBinding;
    Dialog notificationDialog;
    DatabaseReference reference;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null ) {
            reference = FirebaseDatabase.getInstance(Constants.DATABASE_URL).getReference();
            initListeners();
            initNotificationDialog();
        }
    }
    private void initListeners() {
        binding.sendBtn.setOnClickListener(v -> notificationDialog.show());
    }

    private void initNotificationDialog() {
        notificationDialogBinding = DialogNotificationBinding.inflate(getLayoutInflater());
        notificationDialog = new Dialog(requireActivity());
        notificationDialog.setContentView(notificationDialogBinding.getRoot());
        notificationDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        notificationDialogBinding.notificationDialogSendNotification.setOnClickListener(v -> {
            String title = notificationDialogBinding.notificationDialogTitle.getText().toString();
            String message = notificationDialogBinding.notificationDialogMessage.getText().toString();

            FCMSend.pushNotificationToAllUsers(requireActivity(), Constants.TOPIC, title, message, null);

            notificationDialog.dismiss();
        });

    }
}