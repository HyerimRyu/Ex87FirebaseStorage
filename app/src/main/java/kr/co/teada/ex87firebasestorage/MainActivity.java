package kr.co.teada.ex87firebasestorage;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView iv;

    Uri imgUri; //갤러리에서 선택한 이미지 Uri : 이 액티비티 내에 어디서든 알아듣게 전역변수로 뽑아놔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv=findViewById(R.id.iv);

    }//end of onCreate

    public void clickLoad(View view) {

        //Firebase 저장소에 저장되어 있는 이미지 파일을 읽어오기

        //1. FirebaseDataStorage 관리 객체 얻어오기
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();

        //2. 최상위노드 참조객체 얻어오기
        StorageReference rootRef=firebaseStorage.getReference();

        //읽어오길 원하는 파일의 참조객체 얻어오기
        StorageReference imgRef=rootRef.child("f02_real_tasty_stake.jpg"); //이 그림 참조한거야
        imgRef=rootRef.child("ate/f09_pasta.jpg");

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //iv.setImageURI(uri);  //이거 말고 글라이드 써 thread 안써도 돼. 인터넷은 파이어베이스 라이브러리에 있어
                Glide.with(MainActivity.this).load(uri);
            }
        });
    }


    public void clickSelect(View view) {
        //사진선택 앱(갤러리앱, 사진앱 등)을 실행시키고 그 결과 받기
        Intent intent=new Intent(Intent.ACTION_PICK); //묵시적 인텐트
        intent.setType("image/*"); //모든 이미지
        startActivityForResult(intent,10);
    }

    //갤러리 앱의 선택결과를 돌려받을 때 자동으로 실행되는 콜백메소드


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if (resultCode==RESULT_OK){
                    imgUri=data.getData();
                    Glide.with(this).load(imgUri).into(iv); //내가 선택한 이미지가 보여
                }
                break;
        }
    }

    public void clickSave(View view) {
        //save 눌렀을 때 업로드
        if (imgUri==null) return; //아무것도 안할꺼야

        //Firebase저장 - FirebaseStorage 관리객체 얻어오기
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance(); //얘도 그냥 전역변수로 뽑아 놓는게 편해 //getInstance 새로운 노드 생성

        //저장할 파일명으로 노드 참조객체 생성 : 같은 이름 있으면 덮어쓰기 돼 : 값 바꿀때가 덮어쓰기
        //중복되지 않는 이름으로 파일명을 생성하기 위해 날짜 이용
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName=sdf.format( new Date() ) + ".png"; //오늘 날짜로 위에 포맷으로 만들어라

        StorageReference imgRef=firebaseStorage.getReference("uploads/"+fileName); //()안에 안쓰면 root, ()에 바로 쓰면 child 로 바로가

        //노드 참조객체(imgRef)에 파일 업로드하기..
        //imgRef.putFile(imgUri);
        
        //업로드 결과를 처리하고 싶다면..
        UploadTask uploadTask=imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
            }
        });

    }
}//end of MainActivity
