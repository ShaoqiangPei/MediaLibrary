package com.mediamodule.process;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import com.mediamodule.util.MediaLog;
import com.mediamodule.util.StringUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Title:分离视频文件帮助类
 * description:
 * autor:pei
 * created on 2021/3/4
 */
public class SplitMediaFile {

    //分离出的视频文件后缀
    private static final String VIDEO_FILE_SUFFIX=".h264";
    //分离后的视频处理成MP4格式
    private static final String AUDIO_FILE_SUFFIX_BY_MP4=".mp4";
    //分离出的音频文件后缀
    private static final String AUDIO_FILE_SUFFIX=".aac";
    //分离后的音频处理成MP3格式
    private static final String AUDIO_FILE_SUFFIX_BY_MP3=".mp3";
    //视频轨道
    private static final String VIDEO_CHANNEL="video";
    //音频轨道
    private static final String AUDIO_CHANNEL="audio";

    private MediaExtractor mediaExtractor;
    private File mVideoFile;//分离后生成的无声视频文件
    private File mAudioFile;//分离后生成的音频文件

    /****
     * 分离视频的方法
     *
     * @param sourceFilePath 要分离成音频和无声视频的视频源文件地址
     *                       路径中含文件后缀。
     *
     * @param videoFilePath 要存储的无声视频文件地址,为 null 表示不分离出无声视频文件
     *                      路径中不含文件后缀。(生成新的视频文件格式为 MP4)
     *
     * @param audioFilePath 要存储的音频文件地址,为 null 表示不分离出音频文件
     *                      路径中不含文件后缀。(生成新的视频文件格式为 MP3)
     */
    public void split(String sourceFilePath,String videoFilePath,String audioFilePath) {
        if (StringUtil.isEmpty(sourceFilePath)) {
            MediaLog.i("======视频文件路径不能为null==");
            return;
        }
        if(StringUtil.isEmpty(videoFilePath)&&StringUtil.isEmpty(audioFilePath)){
            MediaLog.i("=====videoFilePath,audioFilePath为null表示不分离源文件=====");
            return;
        }
        //创建视频文件
        if(StringUtil.isNotEmpty(videoFilePath)){
            String videoPath=videoFilePath+SplitMediaFile.AUDIO_FILE_SUFFIX_BY_MP4;
            mVideoFile=reCreateFileByPath(videoPath);
            if(mVideoFile!=null){
                MediaLog.i("=====创建视频文件成功===videoPath="+videoPath);
            }
        }
        //创建音频文件
        if(StringUtil.isNotEmpty(audioFilePath)){
            String audioPath=audioFilePath+SplitMediaFile.AUDIO_FILE_SUFFIX_BY_MP3;
            mAudioFile=reCreateFileByPath(audioPath);
            if(mAudioFile!=null){
                MediaLog.i("=====创建音频文件成功===audioPath="+audioPath);
            }
        }

        try {
            mediaExtractor = new MediaExtractor();//此类可分离视频文件的音轨和视频轨道
            mediaExtractor.setDataSource(sourceFilePath);//媒体文件的位置
            int trackCount = mediaExtractor.getTrackCount();
            MediaLog.i("==========trackCount="+trackCount);
            //遍历媒体轨道，包括视频和音频轨道
            for(int i=0;i<trackCount;i++) {
                MediaFormat format = mediaExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                //分离 视频/音频 文件
                splitFile(format,mime,i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放资源
            mediaExtractor.release();
            mediaExtractor = null;
        }
        StringBuffer printBuffer=new StringBuffer("===========分离程序执行结束: ");
        if(mVideoFile!=null){
            printBuffer.append(" mVideoFile.length="+mVideoFile.length());
        }
        if(mAudioFile!=null){
            printBuffer.append(" mAudioFile.length="+mAudioFile.length());
        }
        MediaLog.i(printBuffer.toString());
    }

    /***
     * 根据文件路径创建文件(若文件存在则先删除再创建)
     *
     * @param filePath
     * @return 返回 null表示创建失败
     */
    private File reCreateFileByPath(String filePath) {
        if (StringUtil.isNotEmpty(filePath)) {
            File file = new File(filePath);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /***
     * 得到分离出的文件
     *
     * 若分离出的为音频文件，则为 MP3 格式
     * 若分离出的为视频文件，则为无声的 MP4 格式
     *
     * @param format
     * @param mime
     * @param trackIndex
     */
    private void splitFile(MediaFormat format, String mime, int trackIndex){
        MediaLog.i("========mime="+mime);

        File file=null;
        //获取 视频/音频 轨道
        if (mime.startsWith(SplitMediaFile.VIDEO_CHANNEL)) {
            file=mVideoFile;
        }else if(mime.startsWith(SplitMediaFile.AUDIO_CHANNEL)){
            file=mAudioFile;
        }
        if(file!=null){
            try {
                //选择此 视频/音频 轨道
                mediaExtractor.selectTrack(trackIndex);
                //合成 MP4/MP3 文件
                MediaMuxer mediaMuxer = new MediaMuxer(file.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                int writeIndex = mediaMuxer.addTrack(format);
                mediaMuxer.start();
                //获取 视频/音频 的输出缓存的最大大小
                int maxBufferCount = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                ByteBuffer byteBuffer = ByteBuffer.allocate(maxBufferCount);
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                long stampTime = 0;
                mediaExtractor.readSampleData(byteBuffer, 0);
                long secondTime = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                mediaExtractor.readSampleData(byteBuffer, 0);
                long thirdTime = mediaExtractor.getSampleTime();
                stampTime = Math.abs(thirdTime - secondTime);
                MediaLog.i("====stampTime====="+stampTime);
                while (true) {
                    int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                    if (readSampleSize < 0) {
                        break;
                    }
                    mediaExtractor.advance();
                    bufferInfo.size = readSampleSize;
                    bufferInfo.flags = mediaExtractor.getSampleFlags();
                    bufferInfo.offset = 0;
                    bufferInfo.presentationTimeUs += stampTime;
                    mediaMuxer.writeSampleData(writeIndex, byteBuffer, bufferInfo);
                }
                mediaMuxer.stop();
                mediaMuxer.release();

                if (mime.startsWith(SplitMediaFile.VIDEO_CHANNEL)) {
                    MediaLog.i("分离视频完成+++++++++++++++");
                }else if(mime.startsWith(SplitMediaFile.AUDIO_CHANNEL)){
                    MediaLog.i("分离音频完成+++++++++++++++");
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (mime.startsWith(SplitMediaFile.VIDEO_CHANNEL)) {
                    MediaLog.i("分离视频失败+++++++++++++++");
                }else if(mime.startsWith(SplitMediaFile.AUDIO_CHANNEL)){
                    MediaLog.i("分离音频失败+++++++++++++++");
                }
            }
        }
    }

}
