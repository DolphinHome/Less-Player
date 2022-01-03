package xyz.less.graphic.view;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import xyz.less.bean.Audio;
import xyz.less.bean.Resources.Fxmls;
import xyz.less.bean.Resources.Images;
import xyz.less.bean.Resources.Styles;
import xyz.less.graphic.Guis;
import xyz.less.graphic.control.PlaylistItem;
import xyz.less.graphic.skin.SimpleSkin;
import xyz.less.media.FxMediaPlayer;

public class PlaylistView extends StageView {
	//TODO
	private FxMediaPlayer mediaPlayer;
	
//	private double openerX;
	private double openerY;
	
	private Label logoSizeLbl;
	AnchorPane topPane;
	private ListView<Audio> listView;
	
	private boolean attach = true;
	private boolean lyricOn = false;
	private boolean autoTarget = true;
	private final static double ROW_WIDTH = 335;
	private final static double DURATION_WIDTH = 72;
//	private final static double ROW_PADDING = 3;
	private final Consumer<Void> defaultAttachAction = v -> {
//		openerX = opener.getX();
		openerY = opener.getY();
		double heightDist1 = getHeight() - opener.getHeight();
		double heightDist2 = heightDist1 - SimpleSkin.LYRIC_HEIGHT - SimpleSkin.LYRIC_PADDING_Y;
//		double paddingY = lyricOn ? 18 : 88;
		double paddingY = lyricOn ? heightDist2 / 2 : heightDist1 / 2;
		
		setX(opener.getX() + opener.getWidth() + SimpleSkin.PLAYLIST_PADDING_X);
		setY(openerY - paddingY);
	};
	private Consumer<Void> attachAction;
	
	public PlaylistView(Stage opener, FxMediaPlayer mediaPlayer) {
		super(opener, SimpleSkin.PLAYLIST_WIDTH, SimpleSkin.PLAYLIST_HEIGHT);
		this.mediaPlayer = mediaPlayer;
		setAttachAction(defaultAttachAction);
		
		initGraph();
		initEvents();
	}
	
	private void setAttach(boolean attach) {
		this.attach = attach;
	}
	
	public void setAttachAction(Consumer<Void> attachAction) {
		this.attachAction = attachAction;
	}

	private void setAutoTarget(boolean autoTarget) {
		this.autoTarget = autoTarget;
	}
	
	private void initEvents() {
		setOnShowing(e -> {
			attach();
			highlightCurrentPlaying();
		});
//		setOnHiding(e -> {
//			opener.setX(openerX);
//		});
	}

	protected void initGraph() {
		setSceneRoot(Guis.loadFxml(Fxmls.PLAYLIST_VIEW));
		addStyle(Styles.PLAYLIST_VIEW);
		
		initTop();
		initCenter();
	}

	public void setTopVisible(boolean visible) {
		BorderPane pane = byId("playlist_view");
		if(visible) {
			pane.setTop(topPane);
		} else {
			pane.setTop(null);
		}
	}
	
	private void initTop() {
		topPane = byId("playlist_top");
		
		logoSizeLbl = byId("logo_size");
		Label attachBtn = byId("attach_btn");
		Label targetBtn = byId("target_btn");
		Label closeBtn = byId("close_btn");
		HBox winBtnsBox = byId("win_btns_box");
		
		AnchorPane.setLeftAnchor(logoSizeLbl, 6.0);
		AnchorPane.setRightAnchor(winBtnsBox, 2.0);
		
		ImageView logo = new ImageView(Images.PLAYLIST[1]);
		Guis.bind(logoSizeLbl.prefHeightProperty(), topPane.prefHeightProperty());
		Guis.bind(winBtnsBox.prefHeightProperty(), topPane.prefHeightProperty());
		
		Guis.setFitSize(24, logo);
		Guis.setAlignment(Pos.CENTER_LEFT, logoSizeLbl);
		Guis.setAlignment(Pos.CENTER, winBtnsBox);
		Guis.setPickOnBounds(true, attachBtn, targetBtn, closeBtn);
		
		Guis.setGraphic(logo, logoSizeLbl);
		Guis.setGraphic(Images.ATTACH[1], attachBtn);
		Guis.setGraphic(Images.TARGET[1], targetBtn);
		Guis.setGraphic(Images.CLOSE, closeBtn);
		Guis.setUserData(1, targetBtn, attachBtn);
		
		Guis.addStyleClass("bottom-border-dark", topPane);
		Guis.addStyleClass("logo-label", logoSizeLbl);
		Guis.addStyleClass("label-btn", attachBtn, targetBtn, closeBtn);
		Guis.addHoverStyleClass("label-hover", attachBtn, targetBtn);
		Guis.addHoverStyleClass("label-hover-red", closeBtn);
		
		Guis.addDnmAction(this, topPane, winBtnsBox);
		
		attachBtn.setOnMouseClicked(e -> {
			setAttach(!attach);
			Guis.toggleImage(attachBtn, Images.ATTACH);
		});
		
		targetBtn.setOnMouseClicked(e -> {
			setAutoTarget(!autoTarget);
			Guis.toggleImage(targetBtn, Images.TARGET);
			targetCurrentPlaying();
		});
		
		closeBtn.setOnMouseClicked(e -> hide());
	}

	private void initCenter() {
		listView = byId("list_view");
		listView.setCellFactory(lv -> {
			PlaylistItem item = new PlaylistItem();
			item.setItemWidth(ROW_WIDTH);
			item.setTitleWidth(ROW_WIDTH - DURATION_WIDTH);
			item.setDurationWidth(DURATION_WIDTH);
			item.setOnMouseClicked(e -> {
				if(e.getClickCount() > 1) {
					mediaPlayer.play(indexOf(item.getItem()));
				}
			});
			return item;
		});
		updateGraph();
	}
	
	private int indexOf(Audio item) {
		return listView.getItems().indexOf(item);
	}
	
	private int currentIndex() {
		return mediaPlayer.getCurrentIndex();
	}
	
	public void targetCurrentPlaying() {
		if(autoTarget) {
			listView.scrollTo(currentIndex());
		}
	}
 	
	public void highlightCurrentPlaying() {
		//TODO
//		String styleClass = "current";
		listView.getItems().forEach(item -> {
			item.setPlaying(indexOf(item) == currentIndex());
		});
		listView.refresh();
		targetCurrentPlaying();
	}

	public int size() {
		return listView.getItems().size();
	}
	
	public void resetGraph(boolean forceClose) {
		listView.getItems().remove(0, size());
		updateLogoSizeLabelText();
		if(forceClose) {
			hide();
		}
	}

	public void updateGraph() {
		resetGraph(false);
		List<Audio> datas = mediaPlayer.getPlaylist().get();
		listView.getItems().addAll(datas);
		updateLogoSizeLabelText();
	}
	
	public void updateLogoSizeLabelText() {
		String size = size() > 0 ? size() + "首" : "";
		String text = String.format("当前播放 %1$s", size);
		logoSizeLbl.setText(text);
	}

	@Override
	public void attach() {
		attach(this.lyricOn);
	}
	
	public void attach(boolean lyricOn) {
		this.lyricOn = lyricOn;
		if(attach) {
			locate2Opener();
		}
	}
	
	private void locate2Opener() {
		if(attachAction != null) {
			attachAction.accept(null);
		}
	}
	
}