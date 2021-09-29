package com.example.aplikasipengumuman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Ini adalah project aplikasi untuk mata kuliah TIK3171 - PENGEMBANGAN APLIKASI MOBILE
    // yang diampu oleh Mner SHERWIN REINALDO U ALDO SOMPIE ST, MT dan Mner XAVERIUS B.N. NAJOAN ST, MT
    // Project ini dibuat oleh Fransiscus Xaverius Senduk (NIM 19021106039)
    private int unicode = 0x1F49D;

    public static final String TAG = "MainActivity";
    private EditText etJudul, etIsi;
    private Button btnKirim, btnTampilkan, btnUpdate, btnDelete;
    private TextView tvJudul, tvIsi, tvCredits;

    // Keys
    public static final String KEY_JUDUL = "judul";
    public static final String KEY_ISI = "isi";

    // Koneksi ke Firebase Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference pengumuman = db.collection("Announcement").document("Pertama");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        etJudul = findViewById(R.id.editTextTextPersonName2);
        etIsi = findViewById(R.id.editTextTextMultiLine);
        btnKirim = findViewById(R.id.btnKirim);
        btnTampilkan = findViewById(R.id.btnTampil);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        tvJudul = findViewById(R.id.tvJudulPengumuman);
        tvIsi = findViewById(R.id.tvIsiPengumuman);
        tvCredits = findViewById(R.id.tvCredits);

        String em = new String(Character.toChars(unicode));
        tvCredits.setText("Made with " + em + " by Fransiscus Xaverius Senduk - 19021106039");

        // Tombol untuk kirim data
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = etJudul.getText().toString().trim();
                String isi = etIsi.getText().toString().trim();

                Map<String, Object> data = new HashMap<>();
                data.put(KEY_JUDUL, judul);
                data.put(KEY_ISI, isi);

                pengumuman.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Berhasil mengirim data", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure " + e.toString());
                    }
                });
            }
        });

        // Tombol untuk menampilkan data ke card
        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pengumuman.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String judul = documentSnapshot.getString(KEY_JUDUL);
                            String isi = documentSnapshot.getString(KEY_ISI);
                            tvJudul.setText(judul);
                            tvIsi.setText(isi);
                        } else {
                            Toast.makeText(MainActivity.this, "Tidak ada data di Firestore", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure " + e.toString());
                    }
                });
            }
        });

        // Tombol untuk update data di database (hanya judul)
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String judul = etJudul.getText().toString().trim();

                Map<String, Object> data = new HashMap<>();
                data.put(KEY_JUDUL, judul);

                pengumuman.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Judul berhasil diupdate", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"onFailure " + e.toString());
                    }
                });
            }
        });

        // Tombol untuk hapus data (hanya judul)
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pengumuman.update(KEY_JUDUL, FieldValue.delete());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        pengumuman.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "Terjadi kesalahan!", Toast.LENGTH_LONG).show();
                }
                if (value != null && value.exists()) {
                    String judul = value.getString(KEY_JUDUL);
                    String isi = value.getString(KEY_ISI);
                    tvJudul.setText(judul);
                    tvIsi.setText(isi);
                }
            }
        });
    }
}