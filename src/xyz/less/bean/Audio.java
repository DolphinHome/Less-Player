package xyz.less.bean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import xyz.less.util.StringUtil;

public class Audio implements Comparable<Audio> {
	private String title;
	private String artist;
	private String album;
	private double duration;
	private Image coverArt;
	private String source;
	private BooleanProperty playing = new SimpleBooleanProperty(false);
	
	public Audio() {
		
	}
	
	public Audio(String title, String artist, String album, 
			double duration, Image coverArt, String source) {
		super();
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.duration = duration;
		this.coverArt = coverArt;
		this.source = source;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public Image getCoverArt() {
		return coverArt;
	}
	public void setCoverArt(Image coverArt) {
		this.coverArt = coverArt;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public boolean isPlaying() {
		return playing.get();
	}
	public void setPlaying(boolean playing) {
		this.playing.set(playing);;
	}
	public BooleanProperty playingProperty() {
		return playing;
	}
	
	public boolean equals(Audio o) {
		return compareTo(o) == 0;
	}
	
	@Override
	public int compareTo(Audio o) {
		if(o == null || StringUtil.isEmpty(o.getSource())) {
			return 1;
		}
		//排序规则: 目录优先, 名称次之（不区分大小写）
		return this.source.compareToIgnoreCase(o.getSource());
	}
}