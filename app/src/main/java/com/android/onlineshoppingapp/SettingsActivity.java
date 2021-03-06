package com.android.onlineshoppingapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlineshoppingapp.adapters.ViewPagerAdapterSettings;
import com.android.onlineshoppingapp.models.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private CardView cardLogout, cardChangePass, cardDeleteAccount, cardPolicy, cardVersion;
    private ViewPagerAdapterSettings viewPagerAdapterSettings;
    private TabLayout tabLayoutSettings;
    private ViewPager2 viewPager2Settings;
    private String[] titles = new String[]{"C?? nh??n", "?????a ch???"};

    private FirebaseAuth fAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        viewPagerAdapterSettings = new ViewPagerAdapterSettings(this);

        //init
        ivBack = findViewById(R.id.ivBackToProfile);
        viewPager2Settings = findViewById(R.id.viewpagerSettings);
        tabLayoutSettings = findViewById(R.id.tablayoutSettings);
        cardChangePass = findViewById(R.id.cardChangePass);
        cardDeleteAccount = findViewById(R.id.cardDeleteAccount);
        cardPolicy = findViewById(R.id.cardPolicy);
        cardVersion = findViewById(R.id.cardVersion);
        cardLogout = findViewById(R.id.cardLogout);


        // click on back button
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // viewpager and tab layout
        viewPager2Settings.setAdapter(viewPagerAdapterSettings);
        new TabLayoutMediator(tabLayoutSettings, viewPager2Settings,
                ((tab, position) -> tab.setText(titles[position]))).attach();

        // click on change password
        cardChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        // click on delete account
        cardDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

        // click on policy
        cardPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                alertDialog.setTitle("Ch??nh s??ch")
                        .setMessage(policyContent())
                        .setPositiveButton("????ng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        // click on version
        cardVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this, "B???n ???? c???p nh???t phi??n b???n m???i nh???t", Toast.LENGTH_SHORT).show();
            }
        });

        // click on logout
        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log out
                fAuth.signOut();
                // navigate to login activity
                finishAffinity();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

    }

    // ------------------ Function --------------------------

    private void deleteAccount() {

        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_deleteacc, null);
        BottomSheetDialog bottomSheetDialogDeleteAcc = new BottomSheetDialog(SettingsActivity.this);
        bottomSheetDialogDeleteAcc.setContentView(sheetView);
        bottomSheetDialogDeleteAcc.setCancelable(false);
        bottomSheetDialogDeleteAcc.show();

        // click on close bottom sheet
        sheetView.findViewById(R.id.bottomSheetDeleteAccClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close bottom sheet dialog
                bottomSheetDialogDeleteAcc.dismiss();
            }
        });

        // check email
        TextInputEditText etEmail = sheetView.findViewById(R.id.bottomSheetDeleteAccEmail);
        TextInputLayout layoutEmail = sheetView.findViewById(R.id.layout_bottomSheetDeleteAccEmail);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etEmail.getText().toString().trim().equals("")) {
                        layoutEmail.setHelperText("Email kh??ng ???????c ????? tr???ng!");
                    } else if (!isCorrectEmailFormat(etEmail.getText().toString().trim())) {
                        layoutEmail.setHelperText("Email b???n v???a nh???p kh??ng ????ng ?????nh d???ng");
                    } else if (!etEmail.getText().toString().trim().equals(fAuth.getCurrentUser().getEmail())) {
                        layoutEmail.setHelperText("Email ch??a ????ng k??");
                    }
                } else {
                    layoutEmail.setHelperTextEnabled(false);
                }
            }
        });

        //check password
        TextInputEditText etPassword = sheetView.findViewById(R.id.bottomSheetDeleteAccPass);
        TextInputLayout layoutPassword = sheetView.findViewById(R.id.layout_bottomSheetDeleteAccPass);
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etPassword.getText().toString().equals("")) {
                        layoutPassword.setHelperText("M???t kh???u kh??ng ???????c ????? tr???ng");
                    } else {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), etPassword.getText().toString());
                        user.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                layoutPassword.setHelperText("M???t kh???u b???n nh???p kh??ng ????ng");
                            }
                        });
                    }
                } else {
                    layoutPassword.setHelperTextEnabled(false);
                }
            }
        });

        // click on send code
        Button btnSendCode = sheetView.findViewById(R.id.btnSendDeleteCode);
        TextView tvTimer = sheetView.findViewById(R.id.tvDeleteAccTimer);

        Random random = new Random();
        String verificationCode = String.valueOf(random.nextInt(999999 - 100000) + 100000);
        btnSendCode.setOnClickListener(new View.OnClickListener() {
            private int time = 30;

            @Override
            public void onClick(View view) {
                // send code to user email
                sendCodeByEmail(etEmail.getText().toString().trim(), verificationCode);
                System.out.println("VERIFICATION CODE: " + verificationCode);
                closeKeyboard(sheetView);

                // delay button send code
                btnSendCode.setEnabled(false);
                tvTimer.setVisibility(View.VISIBLE);
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        tvTimer.setText(String.format("G???i l???i sau: %s", String.valueOf(time)));
                        time--;
                    }

                    public void onFinish() {
                        btnSendCode.setEnabled(true);
                        tvTimer.setVisibility(View.GONE);
                    }

                }.start();
            }
        });

        // check code
        TextInputEditText etVerify = sheetView.findViewById(R.id.bottomSheetDeleteAccVerify);
        TextInputLayout layoutVerify = sheetView.findViewById(R.id.layout_bottomSheetDeleteAccVerify);

        layoutVerify.setEndIconOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if (!etVerify.getText().toString().equals(verificationCode)) {
                    layoutVerify.setHelperText("M?? x??c minh kh??ng ????ng");
                } else {
                    layoutVerify.setHelperText("M?? x??c minh h???p l???");
                    layoutVerify.setHelperTextColor(ColorStateList.valueOf(R.color.light_green));
                }
                closeKeyboard(sheetView);
            }
        });

        // click on checkbox
        CheckBox cbConfirmDeleteAcc = sheetView.findViewById(R.id.cbConfirmDeleteAcc);
        Button confirmBtn = sheetView.findViewById(R.id.bottomSheetDeleteAccBtn);

        cbConfirmDeleteAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && etVerify.getText().toString().equals(verificationCode)) {
                    confirmBtn.setEnabled(true);
                } else {
                    confirmBtn.setEnabled(false);
                }
            }
        });


        // click on confirm button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete account here
                Toast.makeText(SettingsActivity.this, "Xo?? t??i kho???n th??nh c??ng", Toast.LENGTH_SHORT).show();

                // navigate to login activity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetDialogDeleteAcc.dismiss();
                        fAuth.signOut();
                        finishAffinity();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    }
                }, 1000);
            }
        });

    }

    private void changePassword() {

        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout_changepass, null);
        BottomSheetDialog bottomSheetDialogChangePass = new BottomSheetDialog(SettingsActivity.this);
        bottomSheetDialogChangePass.setContentView(sheetView);
        bottomSheetDialogChangePass.setCancelable(false);
        bottomSheetDialogChangePass.show();

        // click on close bottom sheet
        sheetView.findViewById(R.id.bottomSheetChangePassClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close bottom sheet dialog
                bottomSheetDialogChangePass.dismiss();
            }
        });

        //check old password
        TextInputEditText etOldPassword = sheetView.findViewById(R.id.bottomSheetChangePassOldPass);
        TextInputLayout layoutOldPassword = sheetView.findViewById(R.id.layout_bottomSheetChangePassOldPass);
        etOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etOldPassword.getText().toString().equals("")) {
                        layoutOldPassword.setHelperText("M???t kh???u kh??ng ???????c ????? tr???ng");
                    } else {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), etOldPassword.getText().toString());
                        user.reauthenticate(credential).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                layoutOldPassword.setHelperText("M???t kh???u b???n nh???p kh??ng ????ng");
                            }
                        });
                    }
                } else {
                    layoutOldPassword.setHelperTextEnabled(false);
                }
            }
        });

        // Check input data: new password
        TextInputEditText etNewPassword = sheetView.findViewById(R.id.bottomSheetChangePassNewPass);
        TextInputLayout layoutNewPassword = sheetView.findViewById(R.id.layout_bottomSheetChangePassNewPass);
        etNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (etNewPassword.getText().toString().equals("")) {
                        layoutNewPassword.setHelperText("M???t kh???u kh??ng ???????c ????? tr???ng!");
                    } else if (!isLongEnough(etNewPassword.getText().toString(), 8)) {
                        layoutNewPassword.setHelperText("M???t kh???u ph???i c?? ??t nh???t 8 k?? t???");
                    } else if (isCorrectTextFormat(etNewPassword.getText().toString())) {
                        layoutNewPassword.setHelperText("M???t kh???u ch??? bao g???m s???, ch??? c??i v?? c??c k?? t??? _ . -");
                    }
                } else {
                    layoutNewPassword.setHelperTextEnabled(false);
                }
            }
        });

        // Check input data: new re-password
        Button confirmBtn = sheetView.findViewById(R.id.bottomSheetChangePassBtn);
        TextInputEditText etReNewPassword = sheetView.findViewById(R.id.bottomSheetChangePassReNewPass);
        TextInputLayout layoutReNewPassword = sheetView.findViewById(R.id.layout_bottomSheetChangePassReNewPass);
        etReNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etReNewPassword.getText().toString().equals(etNewPassword.getText().toString())) {
                    layoutReNewPassword.setHelperTextEnabled(false);
                    confirmBtn.setEnabled(true);
                } else {
                    layoutReNewPassword.setHelperText("X??c nh???n m???t kh???u kh??ng ????ng!");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.updatePassword(etNewPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated.");
                                    Toast.makeText(SettingsActivity.this, "M???t kh???u ???? ???????c c???p nh???t", Toast.LENGTH_SHORT).show();
                                    // require user re login
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            bottomSheetDialogChangePass.dismiss();
                                            fAuth.signOut();
                                            finishAffinity();
                                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                        }
                                    }, 1000);
                                }
                            }
                        });
            }
        });
    }

    private void sendCodeByEmail(String receiverEmail, String verificationCode) {

        try {
            String senderEmail = "project.onlineshoppingapp@gmail.com";
            String senderPassword = "hulwohxxyuihmesr";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            // send email to user: receiverEmail
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            mimeMessage.setSubject("[OnlineShoppingApp] M?? x??c minh y??u c???u xo?? t??i kho???n");
            mimeMessage.setText("Xin ch??o," +
                    "\n\nB???n v???a y??u c???u m?? x??c minh ????? xo?? t??i kho???n c???a b???n " +
                    "v???i ?????a ch??? email: " + receiverEmail +
                    "\n\nM?? x??c minh: " + verificationCode +
                    "\n\nVui l??ng nh???p m?? x??c minh b??n tr??n ????? ch??ng t??i c?? th??? ti???p t???c th???c hi???n " +
                    "c??c b?????c xo?? t??i kho???n c???a b???n." +
                    "\n\nTr??n tr???ng,\n?????i ng?? OnlineShoppingApp");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private void closeKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isCorrectEmailFormat(String str) {
        if (str.matches("[a-zA-Z0-9._-]+@[a-z]+\\.[a-zA-Z0-9.]+"))
            return true;
        return false;
    }

    private boolean isCorrectTextFormat(String str) {
        if (str.matches("[^a-zA-Z0-9._-]"))
            return true;
        return false;
    }

    private boolean isLongEnough(String str, int num) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            count++;
        }
        if (count >= num) return true;
        else return false;
    }

    private String policyContent() {
        String content = "1. Trong qu?? tr??nh cung c???p cho Qu?? v??? c??c D???ch V??? (nh?? ???????c ?????nh ngh??a trong ??i???u Kho???n S??? D???ng) ho???c quy???n truy c???p v??o N???n T???ng ???ng d???ng mua s???m tr???c tuy???n (nh?? ???????c ?????nh ngh??a trong ??i???u Kho???n S??? D???ng), ch??ng t??i s??? thu th???p, s??? d???ng, ti???t l???, l??u tr??? v??/ho???c x??? l?? d??? li???u, bao g???m c??? d??? li???u c?? nh??n c???a Qu?? v???. Trong Ch??nh S??ch B???o M???t n??y, ???ng d???ng c??ng s??? ???????c d???n chi???u ?????n (c??c) n???n t???ng c???a Nh?? B??n H??ng (nh?? ???????c ?????nh ngh??a trong Quy Ch??? Ho???t ?????ng) li??n quan.\n" +
                "\n" +
                "2. Ch??nh S??ch B???o M???t n??y thi???t l???p ????? gi??p Qu?? v??? bi???t ???????c c??ch th???c ch??ng t??i thu th???p, s??? d???ng, ti???t l???, l??u tr??? v??/ho???c x??? l?? d??? li???u m?? ch??ng t??i thu th???p v?? nh???n ???????c trong qu?? tr??nh cung c???p c??c D???ch V??? ho???c c???p quy???n truy c???p v??o ???ng d???ng cho Qu?? v???, ng?????i d??ng c???a ch??ng t??i, cho d?? Qu?? v??? ??ang s??? d???ng ???ng d???ng c???a ch??ng t??i v???i t?? c??ch l?? Kh??ch H??ng (nh?? ???????c ?????nh ngh??a trong Quy Ch??? Ho???t ?????ng) ho???c Nh?? B??n H??ng. Ch??ng T??i s??? ch??? thu th???p, s??? d???ng, ti???t l???, l??u tr??? v??/ho???c x??? l?? d??? li???u c?? nh??n c???a Qu?? v??? theo Ch??nh S??ch B???o M???t n??y.\n" +
                "\n" +
                "3. Qu?? v??? c???n ?????c Ch??nh S??ch B???o M???t n??y c??ng v???i b???t k??? th??ng b??o ???????c ??p d???ng n??o kh??c m?? ch??ng t??i c?? th??? ????a ra trong c??c tr?????ng h???p c??? th??? khi ch??ng t??i thu th???p, s??? d???ng, ti???t l??? v??/ho???c x??? l?? d??? li???u c?? nh??n v??? Qu?? v???, ????? Qu?? v??? nh???n th???c ?????y ????? v??? c??ch th???c v?? l?? do ch??ng t??i s??? d???ng d??? li???u c?? nh??n c???a Qu?? v???.";
        return content;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}