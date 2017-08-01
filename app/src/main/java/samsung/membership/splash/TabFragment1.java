package samsung.membership.splash;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by yumin on 2017-07-29.
 */

public class TabFragment1 extends Fragment {

    private Button voiceRec;
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private View v;
    private ListView listView;
    private VoiceListAdapter voiceListAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_fragment_1, container, false);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(v.getContext());
        speechRecognizer.setRecognitionListener(listener);

        voiceRec = (Button) v.findViewById(R.id.voiceRec);
        voiceRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer.startListening(intent);
            }
        });

        voiceListAdapter = new VoiceListAdapter();

        listView = (ListView) v.findViewById(R.id.listview);
        listView.setAdapter(voiceListAdapter);

        return v;
    }

    private RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onResults(Bundle results) {
            // TODO Auto-generated method stub
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Toast.makeText(getActivity().getApplicationContext(), rs[0], Toast.LENGTH_SHORT).show();
            voiceListAdapter.addItem(rs[0]);
            voiceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onError(int error) {
            // TODO Auto-generated method stub
            Log.d("TAG", "ERROR : " + error);
        }

        @Override
        public void onEndOfSpeech() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
            Log.d("TAG", "START");
        }
    };


}
