wav-recorder
============

Introduction
------------

PhoneGap Cordova Plugin for Android Platform. Records uncompressed audio data to disk as WAV format. The audio buffer is accessible from the webview, thus it is possible ro render to the client the audio data in real time.

Initialize Recorder
-------------------

```
var recorder = new martinescu.Recorder(src, [config], [statusCallback], [bufferCallback]);
```
* src - file to write the audio data to, relative to root
* config - configure capture format
  * sampleRate - sample rate: 44100, 22050, 11025, 8000
  * channels - for now only single channel recording is available: 1 or 2
  * encoding - this is the bit rate of the recording: 8 or 16
* statusCallback - callback for recorder status change, including errors. Receives two parameters: first is the status code, the second is the error message, on error. 
* bufferCallback - callback for receiving the current recorder raw audio data
  
  
**Sample Initialization**
```
// status callback 
var statusCallback = function (mediaStatus, error) {
  if (martinescu.Recorder.STATUS_ERROR == mediaStatus) {
    alert(error);
  }
}

// buffer callback
var bufferCallback = function (buffer) {
  console.log(buffer);
}

var recorder = new martinescu.Recorder('/record.wav', { sampleRate: 22050 }, statusCallback, bufferCallback);
```

Start Recording
---------------
Start recording audio data:
```
recorder.record();
```

Stop recording
--------------
```
recorder.stop();
```

Release Recorder
----------------
When done with the recorder, call release() in order to free memory allocated for it.
```
recorder.release();
```

Media Status
------------

```
Recorder.STATUS_INITIALIZING = 'INITIALIZING';
Recorder.STATUS_READY = 'READY';
Recorder.STATUS_RECORDING = 'RECORDING';
Recorder.STATUS_ERROR = 'ERROR';
Recorder.STATUS_STOPPED = 'STOPPED';
```
