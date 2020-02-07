package com.example.android.dictionaryalmighty2;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceForUserInputHistory;
    private DatabaseReference mReferenceForVocabularyList;
    private List<FirebaseDataBaseUserInputHistory> firebaseDataBaseUserInputHistoryList = new ArrayList<>();
    private List<FirebaseDatabaseVocabularyList> firebaseDatabaseVocabularyListList = new ArrayList<>();



    public interface DataStatusForUserInputHistory{
        void DataIsLoaded(List<FirebaseDataBaseUserInputHistory>firebaseDataBaseUserInputHistoryList, List <String> keysForUserInputHistory);
        void DataIsDetected();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    public interface DataStatusForVocabularyList{
        void DataIsLoaded(List<FirebaseDatabaseVocabularyList>firebaseDatabaseVocabularyListList, List <String> keysForVocabularyList);
        void DataIsDetected();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    //Constructor (Select none)
    public FirebaseDatabaseHelper() {

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceForUserInputHistory = mDatabase.getReference("Users' Input History");
        mReferenceForVocabularyList = mDatabase.getReference("Users' Vocabulary List");
    }


    public void readUserInputHistory (final DataStatusForUserInputHistory dataStatus){

        mReferenceForUserInputHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Clear old list records
                firebaseDataBaseUserInputHistoryList.clear();

                //Create a list to store the keys of the "Users' Input History" node
                List <String> keysForUserInputHistory = new ArrayList<>();

                //Create a for loop with a DataSnapshot containing the keys and values of a specific node.
                //The app will take the keys and values from the "Users' Input History" node, using the parameter "dataSnapshot"
                for (DataSnapshot keyNode:dataSnapshot.getChildren()) {

                    //Get the key of this node and save it to the keysForVocabularyList
                    keysForUserInputHistory.add(keyNode.getKey());

                    //Initialize FirebaseDataBaseUserInputHistory Object from the node values
                    FirebaseDataBaseUserInputHistory firebaseDataBaseUserInputHistory = keyNode.getValue(FirebaseDataBaseUserInputHistory.class);

                    //Add the FirebaseDataBaseUserInputHistory Object (its node values) to the firebaseDataBaseUserInputHistoryList
                    firebaseDataBaseUserInputHistoryList.add(firebaseDataBaseUserInputHistory);

                    //Call DataIsLoaded when the firebaseDataBaseUserInputHistoryList is populated
                    dataStatus.DataIsLoaded(firebaseDataBaseUserInputHistoryList,keysForUserInputHistory);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void readVocabularyList (final DataStatusForVocabularyList dataStatus){

        mReferenceForVocabularyList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Clear old list records
                firebaseDatabaseVocabularyListList.clear();

                //Create a list to store the keys of the "Users' Vocabulary List" node
                List <String> keysForVocabularyList = new ArrayList<>();

                //Create a for loop with a DataSnapshot containing the keys and values of a specific node.
                //The app will take the keys and values from the "Users' Vocabulary List" node, using the parameter "dataSnapshot"
                for (DataSnapshot keyNode:dataSnapshot.getChildren()) {

                    //Get the key of this node and save it to the keysForVocabularyList
                    keysForVocabularyList.add(keyNode.getKey());

                    //Initialize FirebaseDatabaseVocabularyList Object from the node values
                    FirebaseDatabaseVocabularyList firebaseDatabaseVocabularyList = keyNode.getValue(FirebaseDatabaseVocabularyList.class);

                    //Add the FirebaseDatabaseVocabularyList Object (its node values) to the firebaseDataBaseUserInputHistoryList
                    firebaseDatabaseVocabularyListList.add(firebaseDatabaseVocabularyList);

                    //Call DataIsLoaded when the firebaseDatabaseVocabularyList List is populated
                    dataStatus.DataIsLoaded(firebaseDatabaseVocabularyListList,keysForVocabularyList);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
