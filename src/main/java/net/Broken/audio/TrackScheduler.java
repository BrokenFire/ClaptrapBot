package net.Broken.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingDeque<AudioTrack> queue;
    Logger logger = LogManager.getLogger();

    /**
     * @param player The audio player this scheduler uses
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        player.setVolume(25);
        this.queue = new LinkedBlockingDeque<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }
    public void addNext(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.addFirst(track);
        }
    }

    public void pause() {
        player.setPaused(true);
    }

    public void resume() {
        player.setPaused(false);

    }

    public void stop(){
        player.stopTrack();
        player.destroy();
    }

    public void flush(){
        queue.clear();
    }

    public List<AudioTrackInfo> getList(){
//        AudioTrack[] test = (AudioTrack[]) queue.toArray();

        List<AudioTrackInfo> temp = new ArrayList<>();
        Object[] test = queue.toArray();
        for(Object track: test){
            AudioTrack casted = (AudioTrack) track;
            temp.add(casted.getInfo());
        }
        return temp;
    }

    public AudioTrackInfo getInfo(){
        return player.getPlayingTrack().getInfo();
    }

    public boolean remove(String uri){
        for(AudioTrack track : queue){
            if(track.getInfo().uri.equals(uri)){
                if(!queue.remove(track)) {
                    logger.info("Delete failure!");
                    return false;
                } else {
                    logger.info("Delete succeful");
                    return true;
                }
            }
        }
        logger.info("Delete failure! Not found.");
        return false;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }


}
