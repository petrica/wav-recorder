package ro.martinescu.audio;

import java.util.HashMap;

import org.apache.cordova.*;
import org.apache.cordova.media.FileHelper;
import org.json.JSONArray;
import org.json.JSONException;

import android.media.AudioFormat;
import android.net.Uri;

public class WAVRecorder extends CordovaPlugin {
	public static String TAG = "WAVRecorder";
	HashMap<String, ExtAudioRecorder> players;

	public WAVRecorder() {
		this.players = new HashMap<String, ExtAudioRecorder>();
	}

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		CordovaResourceApi resourceApi = webView.getResourceApi();
		PluginResult.Status status = PluginResult.Status.OK;
        String result = "";

        if (action.equals("record")) {
            this.startRecording(args.getString(0));
        }
        if (action.equals("recordForMillis")) {
            this.startRecording(args.getString(0), args.getInt(1));
        }
        else if (action.equals("stop")) {
        	this.stopRecording(args.getString(0));
        }
        else if (action.equals("release")) {
        	boolean back = this.release(args.getString(0));
            callbackContext.sendPluginResult(new PluginResult(status, back));
            return true;
        }
        else if (action.equals("create")) {
            String id = args.getString(0);
            String target = args.getString(1);
            String fileUriStr;
            try {
                Uri targetUri = resourceApi.remapUri(Uri.parse(target));
                fileUriStr = targetUri.toString();
            } catch (IllegalArgumentException e) {
                fileUriStr = target;
            }

            int sampleRate = args.getInt(2);
            int channels = args.getInt(3);
            int encoding = args.getInt(4);

            // Determine mono or stereo
            if (channels == 1) channels = AudioFormat.CHANNEL_IN_MONO;
            else channels = AudioFormat.CHANNEL_IN_STEREO;

            // Determine encoding 16bit or 8bit
            if (encoding == 8) encoding = AudioFormat.ENCODING_PCM_8BIT;
            else encoding = AudioFormat.ENCODING_PCM_16BIT;

            String src = FileHelper.stripFileProtocol(fileUriStr);
            ExtAudioRecorder audio = ExtAudioRecorder.getInstance(
            		this,
            		id,
            		sampleRate,
            		channels,
            		encoding
            		);
            audio.setOutputFile(src);

            this.players.put(id, audio);
        }
        else if (action.equals("locate")) {

            String id = args.getString(0);

				ExtAudioRecorder audio = this.players.get(id);

            callbackContext.success(audio.getFilePath());
	}
        else {
        	return false;
        }

        callbackContext.sendPluginResult(new PluginResult(status, result));

        return true;
    }

	public void startRecording(String id) {
		ExtAudioRecorder audio = this.players.get(id);
		if (audio != null) {
			audio.prepare();
			audio.start();
		}
  }


		public void startRecording(String id, int durationMS) {
			ExtAudioRecorder audio = this.players.get(id);
			if (audio != null) {
				audio.prepare();
				audio.recordFor(durationMS);
			}
	  }

	public void stopRecording(String id) {
		ExtAudioRecorder audio = this.players.get(id);
		if (audio != null) {
			audio.stop();
			audio.reset();
		}
	}

	private boolean release(String id) {
        if (!this.players.containsKey(id)) {
            return false;
        }
        ExtAudioRecorder audio = this.players.get(id);
        this.players.remove(id);
        audio.release();
        return true;
    }

	/**
     * Stop all audio recorders on navigate.
     */
    @Override
    public void onReset() {
        onDestroy();
    }

    /**
     * Stop all audio recorders.
     */
    public void onDestroy() {
        for (ExtAudioRecorder audio : this.players.values()) {
            audio.release();
        }
        this.players.clear();
    }

}
