package com.andromob.andronews.activity;

import static com.andromob.andronews.fragments.MyDashboardFragment.isNewsEdited;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andromob.andronews.BuildConfig;
import com.andromob.andronews.R;
import com.andromob.andronews.interfaces.DialogClickListener;
import com.andromob.andronews.models.Category;
import com.andromob.andronews.models.ModelPost;
import com.andromob.andronews.models.News;
import com.andromob.andronews.models.User;
import com.andromob.andronews.utils.AdsManager;
import com.andromob.andronews.utils.ExifUtils;
import com.andromob.andronews.utils.LoginPrefManager;
import com.andromob.andronews.utils.Methods;
import com.andromob.andronews.utils.ProgressLoadingDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewsActivity extends AppCompatActivity {
    AutoCompleteTextView selectCategory, selectType;
    ArrayAdapter<String> catAdapter, typeAdapter;
    List<String> nameList = new ArrayList<String>();
    List<Integer> idList = new ArrayList<Integer>();
    int selectedCategory = 0;
    User user;
    LinearLayout mainLyt, ll_selectImg;
    RelativeLayout lyt_no_internet;
    TextView txtNoConnection, txtErorMsg;
    MaterialButton btnRetry;
    String[] types;
    String selectedType = "";
    TextInputEditText inputTitle, inputVideoUrl;
    TextInputLayout lyt_inputVideoUrl;
    ImageView thumbnail;
    Bitmap thumbnailbitmap = null;
    Uri uri;
    MaterialButton btnSubmit;
    private RichEditor mEditor;
    Methods methods;
    News newsList;
    String type;
    boolean isEditImage = false;
    FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        types = new String[]{this.getResources().getString(R.string.add_news_type_image), this.getResources().getString(R.string.add_news_type_video)};
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.equals("edit")) {
            List<News> data = this.getIntent().getExtras().getParcelableArrayList("data");
            newsList = (News) data.get(0);
            toolbar.setTitle(getString(R.string.edit_news));
        } else {
            toolbar.setTitle(getString(R.string.add_news));
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        user = LoginPrefManager.getInstance(this).getUser();
        methods = new Methods(this, new DialogClickListener() {
            @Override
            public void onClick(String action) {
                switch (action) {
                    case "addSuccess":
                        finish();
                        break;
                    case "updateSuccess":
                        finish();
                        break;
                    case "failed":
                        //
                        break;
                }
            }
        });

        adContainerView = findViewById(R.id.adContainerView);
        selectType = findViewById(R.id.selectType);
        selectCategory = findViewById(R.id.selectCategory);
        mainLyt = findViewById(R.id.mainLyt);
        lyt_no_internet = findViewById(R.id.no_internet);
        txtNoConnection = findViewById(R.id.txtNoConnection);
        txtErorMsg = findViewById(R.id.txtErorMsg);
        btnRetry = findViewById(R.id.btnRetry);

        thumbnail = findViewById(R.id.thumbnail);
        ll_selectImg = findViewById(R.id.ll_selectImg);
        lyt_inputVideoUrl = findViewById(R.id.lyt_inputVideoUrl);
        inputTitle = findViewById(R.id.inputTitle);
        mEditor = (RichEditor) findViewById(R.id.editor);
        inputVideoUrl = findViewById(R.id.inputVideoUrl);
        btnSubmit = findViewById(R.id.btnSubmit);

        if (new Methods(this).isNetworkAvailable()) {
            fetchCategories();
        } else {
            lyt_no_internet.setVisibility(View.VISIBLE);
        }

        btnRetry.setOnClickListener(v -> {
            if (new Methods(this).isNetworkAvailable()) {
                fetchCategories();
            }
        });
        setTypes();
        ll_selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataValidation();
            }
        });
        setupRichEditor();
    }

    void setupRichEditor() {
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setEditorBackgroundColor(Color.TRANSPARENT);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.texteditor_bg);
        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder(getString(R.string.insert_news_content_msg));
        //mEditor.setInputEnabled(false);
        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

    private void setTypes() {
        if (type.equals("edit")) {
            selectType.setText(newsList.getType());
            selectedType = newsList.getType();
        }
        typeAdapter = new ArrayAdapter<String>(AddNewsActivity.this, R.layout.item_list_dropdown, types);
        selectType.setAdapter(typeAdapter);
        selectType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedType = typeAdapter.getItem(position).toString();
                if (selectedType.equalsIgnoreCase("Video")) {
                    lyt_inputVideoUrl.setVisibility(View.VISIBLE);
                } else {
                    lyt_inputVideoUrl.setVisibility(View.GONE);
                }
                selectType.setError(null);
                selectType.clearFocus();
            }
        });
        if (selectedType.equalsIgnoreCase("Video")){
            lyt_inputVideoUrl.setVisibility(View.VISIBLE);
        }
    }

    private void fetchCategories() {
        new ProgressLoadingDialog(this).startLoadingdialog();
        Call<List<Category>> dataCall = Methods.getApi().getCategories("category", BuildConfig.API_KEY);
        dataCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        String name = response.body().get(i).getCategory_name();
                        nameList.add(name);
                        int id = response.body().get(i).getCat_id();
                        idList.add(id);
                    }
                    if (type.equals("edit")) {
                        selectCategory.setText(newsList.getCategory_name());
                        selectedCategory = newsList.getCid();
                        inputTitle.setText(newsList.getNews_title());
                        mEditor.setHtml(newsList.getNews());
                        inputVideoUrl.setText(newsList.getVideo_url());
                        methods.loadNewsImage(thumbnail, newsList.getThumbnail());
                    }
                    catAdapter = new ArrayAdapter<String>(AddNewsActivity.this, R.layout.item_list_dropdown, nameList);
                    selectCategory.setAdapter(catAdapter);
                    selectCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = idList.get(position);
                            selectCategory.setError(null);
                            selectCategory.clearFocus();
                        }
                    });

                    mainLyt.setVisibility(View.VISIBLE);
                } else {
                    new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                    checkInternet();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
            }
        });
    }

    private void checkInternet() {
        String message, sub_msg;
        if (!new Methods(AddNewsActivity.this).isNetworkAvailable()) {
            message = getString(R.string.no_internet_msg);
            sub_msg = getString(R.string.check_internet);
        } else {
            message = getString(R.string.whoops);
            sub_msg = getString(R.string.try_again);
        }
        lyt_no_internet.setVisibility(View.VISIBLE);
        txtNoConnection.setText(message);
        txtErorMsg.setText(sub_msg);
    }

    void dataValidation() {
        final String title = inputTitle.getText().toString().trim();
        final String news = mEditor.getHtml();
        final String video_url = inputVideoUrl.getText().toString().trim();
        if (selectedType.isEmpty()) {
            selectType.setError(getString(R.string.field_empty_msg));
            selectType.requestFocus();
            return;
        }

        if (selectedCategory == 0) {
            selectCategory.setError(getString(R.string.field_empty_msg));
            selectCategory.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            inputTitle.setError(getString(R.string.field_empty_msg));
            inputTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(news)) {
            Toast.makeText(this, getString(R.string.select_image_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (type.equals("add")) {
            if (thumbnailbitmap == null) {
                Toast.makeText(this, getString(R.string.select_image_msg), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (selectedType.equalsIgnoreCase(getString(R.string.add_news_type_video))) {
            if (TextUtils.isEmpty(video_url)) {
                inputVideoUrl.setError(getString(R.string.field_empty_msg));
                inputVideoUrl.requestFocus();
                return;
            }
        }

        if (type.equals("add")) {
            if (selectedType.equalsIgnoreCase(getString(R.string.add_news_type_image))) {
                addNewsData("image", selectedCategory, user.getUser_id(), title, news, "");
            } else {
                addNewsData("video", selectedCategory, user.getUser_id(), title, news, video_url);
            }
        } else if (type.equals("edit")) {
            if (thumbnailbitmap != null) {
                if (selectedType.equalsIgnoreCase(getString(R.string.add_news_type_image))) {
                    updateNews(true, "image", selectedCategory, user.getUser_id(), title, news, "");
                } else {
                    updateNews(true, "video", selectedCategory, user.getUser_id(), title, news, video_url);
                }
            } else {
                if (selectedType.equalsIgnoreCase(getString(R.string.add_news_type_image))) {
                    updateNews(false, "image", selectedCategory, user.getUser_id(), title, news, "");
                } else {
                    updateNews(false, "video", selectedCategory, user.getUser_id(), title, news, video_url);
                }
            }
        }


    }

    private void addNewsData(String news_type, int cid, int uid, String news_title, String news, String video_url) {
        new ProgressLoadingDialog(AddNewsActivity.this).startLoadingdialog();
        Call<List<ModelPost>> postCall = null;
        String uniqueID = UUID.randomUUID().toString();
        File file = new File(getApplicationContext().getCacheDir(), uniqueID + "-temp-profile.png");
        try {
            file.createNewFile();
            Bitmap bitmap = thumbnailbitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(uri)));
        MultipartBody.Part imgBody =
                MultipartBody.Part.createFormData("thumbnail", file.getName(), requestFile);

        RequestBody newsTypeBody = RequestBody.create(news_type, MediaType.parse("multipart/form-data"));
        RequestBody cidBody = RequestBody.create(String.valueOf(cid), MediaType.parse("multipart/form-data"));
        RequestBody uidBody = RequestBody.create(String.valueOf(uid), MediaType.parse("multipart/form-data"));
        RequestBody titleBody = RequestBody.create(news_title, MediaType.parse("multipart/form-data"));
        RequestBody newsBody = RequestBody.create(news, MediaType.parse("multipart/form-data"));
        RequestBody videUrlBody = RequestBody.create(video_url, MediaType.parse("multipart/form-data"));
        RequestBody newsStatusBody = RequestBody.create(String.valueOf(2), MediaType.parse("multipart/form-data"));

        postCall = Methods.getApi().addNews("addNews",
                BuildConfig.API_KEY, newsTypeBody, cidBody, uidBody, titleBody, newsBody, imgBody, videUrlBody, newsStatusBody);

        postCall.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(@NonNull Call<List<ModelPost>> call, @NonNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                    if (!response.body().get(0).isError()) {
                        methods.showDialog(AddNewsActivity.this, getString(R.string.success), getString(R.string.news_added), "success", "addSuccess");
                    } else {
                        methods.showDialog(AddNewsActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                    }
                } else {
                    new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                    methods.showDialog(AddNewsActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ModelPost>> call, @NonNull Throwable t) {
                new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                Toast.makeText(AddNewsActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateNews(Boolean isImage, String news_type, int cid, int uid, String news_title, String news, String video_url) {
        new ProgressLoadingDialog(AddNewsActivity.this).startLoadingdialog();
        Call<List<ModelPost>> postCall = null;
        if (isImage) {
            String uniqueID = UUID.randomUUID().toString();
            File file = new File(getApplicationContext().getCacheDir(), uniqueID + "-temp-profile.png");
            try {
                file.createNewFile();
                Bitmap bitmap = thumbnailbitmap;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(uri)));
            MultipartBody.Part imgBody =
                    MultipartBody.Part.createFormData("thumbnail", file.getName(), requestFile);
            RequestBody newsIdBody = RequestBody.create(String.valueOf(newsList.getId()), MediaType.parse("multipart/form-data"));
            RequestBody newsTypeBody = RequestBody.create(news_type, MediaType.parse("multipart/form-data"));
            RequestBody cidBody = RequestBody.create(String.valueOf(cid), MediaType.parse("multipart/form-data"));
            RequestBody uidBody = RequestBody.create(String.valueOf(uid), MediaType.parse("multipart/form-data"));
            RequestBody titleBody = RequestBody.create(news_title, MediaType.parse("multipart/form-data"));
            RequestBody newsBody = RequestBody.create(news, MediaType.parse("multipart/form-data"));
            RequestBody videUrlBody = RequestBody.create(video_url, MediaType.parse("multipart/form-data"));
            RequestBody newsStatusBody = RequestBody.create(String.valueOf(2), MediaType.parse("multipart/form-data"));
            postCall = Methods.getApi().updateNewsWithImage("updateNews",
                    BuildConfig.API_KEY, newsIdBody, newsTypeBody, cidBody, uidBody, titleBody, newsBody, imgBody, videUrlBody, newsStatusBody);
        } else {
            postCall = Methods.getApi().updateNews("updateNews",
                    BuildConfig.API_KEY, newsList.getId(), news_type, cid, uid, news_title, news, video_url, 2);
        }

        postCall.enqueue(new Callback<List<ModelPost>>() {
            @Override
            public void onResponse(@NonNull Call<List<ModelPost>> call, @NonNull Response<List<ModelPost>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                    if (!response.body().get(0).isError()) {
                        isNewsEdited = true;
                        methods.showDialog(AddNewsActivity.this, getString(R.string.success), getString(R.string.news_updated), "success", "updateSuccess");
                    } else {
                        methods.showDialog(AddNewsActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                    }
                } else {
                    new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                    methods.showDialog(AddNewsActivity.this, getString(R.string.failed), getString(R.string.something_went_wrong), "alert", "failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ModelPost>> call, @NonNull Throwable t) {
                new ProgressLoadingDialog(AddNewsActivity.this).dismissdialog();
                Toast.makeText(AddNewsActivity.this, "Throwable : " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            if (result.getData() != null) {
                                uri = result.getData().getData();
                                thumbnailbitmap = ExifUtils.getRotatedBitmap(getApplicationContext(), uri);
                                thumbnail.setImageBitmap(thumbnailbitmap);
                            }
                        } catch (Exception e) {
                            Log.d("TAG", "" + e.getLocalizedMessage());
                        }
                    }
                }
            });

    private void updateAds(){
        AdsManager.getInstance(this).showBannerAd(this, adContainerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdsManager.getInstance(this).destroyBannerAds();
    }
}