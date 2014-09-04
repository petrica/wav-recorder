var argscheck = require('cordova/argscheck'),
  utils = require('cordova/utils'),
  exec = require('cordova/exec');

var mediaObjects = {};

var sampleConfig = {
  sampleRate: 44100,
  channels: 1,
  encoding: 16
}

var Recorder = function(src, config, statusCallback, bufferCallback) {
  argscheck.checkArgs('S*FF', 'Recorder', arguments);
  this.id = utils.createUUID();
  mediaObjects[this.id] = this;
  this.src = src;
  this.bufferCallback = bufferCallback;
  this.statusCallback = statusCallback;
  // Config defaults
  this.config = {};
  for (var prop in sampleConfig) {
    this.config[prop] = sampleConfig[prop];
  }
  if (config) {
    for (var prop in config) {
      this.config[prop] = config[prop];
    }
  }

  exec(null, null, "WAVRecorder", "create", [this.id,
    this.src,
    this.config.sampleRate,
    this.config.channels,
    this.config.encoding ]);
};

// Media messages
Recorder.MEDIA_STATE = 1;
Recorder.MEDIA_BUFFER = 2;

/**
* Start recorder
  */
Recorder.prototype.record = function(options) {
  exec(null, null, "WAVRecorder", "record", [this.id]);
};

/**
 * Stop recording audio file.
 */
Recorder.prototype.stop = function() {
  exec(null, null, "WAVRecorder", "stop", [this.id]);
};

/**
 * Stop playing audio file.
 */
Recorder.prototype.release = function() {
  exec(null, null, "WAVRecorder", "release", [this.id]);
};

/**
 * Audio has status update.
 * PRIVATE
 *
 * @param id            The media object id (string)
 * @param msgType       The 'type' of update this is
 * @param value         Use of value is determined by the msgType
 */
Recorder.onStatus = function(id, msgType, value) {

  var recorder = mediaObjects[id];

  if(recorder) {
    switch(msgType) {
      case Recorder.MEDIA_STATE :
        recorder.statusCallback && recorder.statusCallback(value);
        break;
      case Recorder.MEDIA_BUFFER :
        recorder.bufferCallback && recorder.bufferCallback(value);
        break;
      default :
        console.error && console.error("Unhandled Media.onStatus :: " + msgType);
        break;
    }
  }
  else {
    console.error && console.error("Received Media.onStatus callback for unknown media :: " + id);
  }

};

module.exports = Recorder;