package com.deqode.android.zulkarnains_1202141255_modul6;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    TextView detailUsername, detailTitle, detailDesc;
    ImageView detailPhoto;
    EditText detailComment;
    Button buttonComment;

    RecyclerView recyclerComment;
    List<Comment> comments;

    DatabaseReference database;
    String username = "", postId = "";
    String message = "";

    FirebaseUser user;
    // dari firebase
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        database = FirebaseDatabase.getInstance().getReference("comments");

        referencing();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
        username = userEmail.substring(0, userEmail.indexOf("@"));

        if (getIntent() != null) {
            detailUsername.setText(username);
            detailTitle.setText(getIntent().getStringExtra("title"));
            detailDesc.setText(getIntent().getStringExtra("desc"));

            postId = getIntent().getStringExtra("id");

            Picasso.get().load(getIntent().getStringExtra("photo")).into(detailPhoto);
        }

        comments = new ArrayList<>();

        recyclerComment.setLayoutManager(new LinearLayoutManager(DetailActivity.this));

        new LoadComment().execute();

        // posting comment
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PostComment().execute();
            }
        });
    }

    private void referencing() {
        detailUsername = findViewById(R.id.detailUsername);
        detailTitle = findViewById(R.id.detailTitle);
        detailDesc = findViewById(R.id.detailDesc);
        detailPhoto = findViewById(R.id.detailPhoto);
        detailComment = findViewById(R.id.detailComment);
        buttonComment = findViewById(R.id.buttonPostComment);
        recyclerComment = findViewById(R.id.recyclerComment);
    }

    public class PostComment extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            message = detailComment.getText().toString();

            String commentId = database.push().getKey();

            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUsername(username);
            comment.setMessage(message);

            database.child(commentId).setValue(comment);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            detailComment.setText("");
            new LoadComment().execute();
        }
    }

    public class LoadComment extends AsyncTask<Void, String, Void> {

        DatabaseReference databaseReference;
        CommentAdapter adapter;

        @Override
        protected Void doInBackground(Void... voids) {
            comments.clear();
            databaseReference = FirebaseDatabase.getInstance().getReference("comments");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (comment.getPostId().equalsIgnoreCase(postId)) {
                            comments.add(comment);
                        }
                    }

                    adapter = new CommentAdapter(DetailActivity.this, comments);
                    recyclerComment.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }
    }
}