# AndroidMp4v2
 先说下，为什么不使用简单便捷的系统API去录制mp4呢。

 使用android系统api常用的方案有，MediaRecoder，或者MediaCodec和MediaMuxer来完成，但是这样做的时候，大家可能会发信，由于android系统层各个厂商的高度定制，很难保证这些api可以稳定的工作。从而要写很多适配特殊设备的代码，即便这样，还是难以保证稳定性，经常会遇见某款手机调用api时崩溃，或者录制的mp4文件不能播放的问题。所以，如何不依赖这些API来完成mp4文件的录制呢，就是本文要介绍的内容。

 解决方案：使用mp4v2及x264开源库的native方案，将camera回调的视频数据先通过x264编译位h264帧，然后通过mp4v2将帧装入mp4的box中，通过AudioRecord回调的PCM音频数据，native库进行编码，放入mp4中。

 可能有些人会说，纯软件方案虽然兼容性得到了保证，但是编码速度肯定没有MediaCodec的硬编码块，从而性能难以保证。软编码确实没有硬编码效率高，但是以目前市面上千元机的性能来看，软编码720P甚至1080P的视频，也会非常流畅。而且，目前很多大厂的小视频方案也都是基于纯软件的方式去做的。
