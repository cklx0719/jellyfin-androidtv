package org.jellyfin.androidtv.itemhandling;

import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import org.jellyfin.androidtv.R;
import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.model.ChapterItemInfo;
import org.jellyfin.androidtv.ui.GridButton;
import org.jellyfin.androidtv.util.ImageUtils;
import org.jellyfin.androidtv.util.TimeUtils;
import org.jellyfin.androidtv.util.Utils;
import org.jellyfin.androidtv.util.apiclient.BaseItemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jellyfin.apiclient.interaction.EmptyResponse;
import org.jellyfin.apiclient.interaction.Response;
import org.jellyfin.apiclient.model.apiclient.ServerInfo;
import org.jellyfin.apiclient.model.dto.BaseItemDto;
import org.jellyfin.apiclient.model.dto.BaseItemPerson;
import org.jellyfin.apiclient.model.dto.UserDto;
import org.jellyfin.apiclient.model.entities.ImageType;
import org.jellyfin.apiclient.model.livetv.ChannelInfoDto;
import org.jellyfin.apiclient.model.livetv.SeriesTimerInfoDto;
import org.jellyfin.apiclient.model.search.SearchHint;

public class BaseRowItem {
    private int index;
    private BaseItemDto baseItem;
    private BaseItemPerson person;
    private ChapterItemInfo chapterInfo;
    private ServerInfo serverInfo;
    private UserDto user;
    private SearchHint searchHint;
    private ChannelInfoDto channelInfo;
    private SeriesTimerInfoDto seriesTimerInfo;
    private GridButton gridButton;
    private ItemType type;
    private boolean preferParentThumb = false;
    protected boolean staticHeight = false;
    private SelectAction selectAction = SelectAction.ShowDetails;
    private boolean isPlaying;


    public BaseRowItem(int index, BaseItemDto item) {
        this(index, item, false, false);
    }

    public BaseRowItem(int index, BaseItemDto item, boolean preferParentThumb, boolean staticHeight) {
        this(index, item, preferParentThumb, staticHeight, SelectAction.ShowDetails);
    }
    public BaseRowItem(int index, BaseItemDto item, boolean preferParentThumb, boolean staticHeight, SelectAction selectAction) {
        this.index = index;
        this.baseItem = item;
        type = item.getType().equals("Program") ? ItemType.LiveTvProgram : item.getType().equals("Recording") ? ItemType.LiveTvRecording : ItemType.BaseItem;
        this.preferParentThumb = preferParentThumb;
        this.staticHeight = staticHeight;
        this.selectAction = selectAction;
    }

    public BaseRowItem(int index, ChannelInfoDto channel) {
        this.index = index;
        this.channelInfo = channel;
        type = ItemType.LiveTvChannel;
    }

    public BaseRowItem(BaseItemDto program, boolean staticHeight) { this(0, program, false, staticHeight);    }

    public BaseRowItem(BaseItemDto program) {
        this(0, program);
    }

    public BaseRowItem(ServerInfo server) {
        this.serverInfo = server;
        this.type = ItemType.Server;
    }

    public BaseRowItem(SeriesTimerInfoDto timer) {
        this.seriesTimerInfo = timer;
        this.type = ItemType.SeriesTimer;
    }

    public BaseRowItem(BaseItemPerson person) {
        this.person = person;
        this.staticHeight = true;
        type = ItemType.Person;
    }

    public BaseRowItem(UserDto user) {
        this.user = user;
        type = ItemType.User;
    }

    public BaseRowItem(SearchHint hint) {
        this.searchHint = hint;
        type = ItemType.SearchHint;
    }

    public BaseRowItem(ChapterItemInfo chapter) {
        this.chapterInfo = chapter;
        this.staticHeight = true;
        type = ItemType.Chapter;
    }

    public BaseRowItem(GridButton button) {
        this.gridButton = button;
        type = ItemType.GridButton;
        staticHeight = true;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int ndx) { index = ndx; }

    public BaseItemDto getBaseItem() {
        return baseItem;
    }
    public BaseItemPerson getPerson() { return person; }
    public ChapterItemInfo getChapterInfo() { return chapterInfo; }
    public ServerInfo getServerInfo() { return serverInfo; }
    public UserDto getUser() { return user; }
    public SearchHint getSearchHint() { return searchHint; }
    public ChannelInfoDto getChannelInfo() { return channelInfo; }
    public BaseItemDto getProgramInfo() { return baseItem; }
    public BaseItemDto getRecordingInfo() { return baseItem; }
    public SeriesTimerInfoDto getSeriesTimerInfo() { return seriesTimerInfo; }
    public GridButton getGridButton() { return gridButton; }

    public boolean isChapter() { return type == ItemType.Chapter; }
    public boolean isPerson() { return type == ItemType.Person; }
    public boolean isBaseItem() { return type == ItemType.BaseItem; }
    public boolean getPreferParentThumb() { return preferParentThumb; }
    public ItemType getItemType() { return type; }
    public boolean isFolder() {
        return type == ItemType.BaseItem && baseItem != null && baseItem.getIsFolderItem();
    }
    public boolean showCardInfoOverlay() {return type == ItemType.BaseItem && baseItem != null
            && ("Folder".equals(baseItem.getType()) || "PhotoAlbum".equals(baseItem.getType()) || "RecordingGroup".equals(baseItem.getType())
            || "UserView".equals(baseItem.getType()) || "CollectionFolder".equals(baseItem.getType()) || "Photo".equals(baseItem.getType())
            || "Video".equals(baseItem.getType()) || "Person".equals(baseItem.getType()) || "Playlist".equals(baseItem.getType())
            || "MusicArtist".equals(baseItem.getType()));
    }

    public boolean isValid() {
        switch (type) {
            case BaseItem:
                return baseItem != null;
            case Person:
                return person != null;
            case Chapter:
                return chapterInfo != null;
            case SeriesTimer:
                return seriesTimerInfo != null;
            default:
                return true; //compatibility
        }
    }

    public String getImageUrl(String imageType, int maxHeight) {
        switch (type) {
            case BaseItem:
            case LiveTvProgram:
            case LiveTvRecording:
                switch (imageType) {
                    case org.jellyfin.androidtv.model.ImageType.BANNER:
                        return ImageUtils.getBannerImageUrl(baseItem, TvApp.getApplication().getApiClient(), maxHeight);
                    case org.jellyfin.androidtv.model.ImageType.THUMB:
                        return ImageUtils.getThumbImageUrl(baseItem, TvApp.getApplication().getApiClient(), maxHeight);
                    default:
                        return getPrimaryImageUrl(maxHeight);
                }
                default:
                    return getPrimaryImageUrl(maxHeight);
        }
    }

    public String getPrimaryImageUrl(int maxHeight) {
        switch (type) {

            case BaseItem:
            case LiveTvProgram:
            case LiveTvRecording:
                return ImageUtils.getPrimaryImageUrl(baseItem, TvApp.getApplication().getApiClient(), preferParentThumb, maxHeight);
            case Person:
                return ImageUtils.getPrimaryImageUrl(person, TvApp.getApplication().getApiClient(), maxHeight);
            case User:
                return ImageUtils.getPrimaryImageUrl(user, TvApp.getApplication().getLoginApiClient());
            case Chapter:
                return chapterInfo.getImagePath();
            case LiveTvChannel:
                return ImageUtils.getPrimaryImageUrl(channelInfo, TvApp.getApplication().getApiClient());
            case Server:
                return "android.resource://org.jellyfin.androidtv/" + R.drawable.server;
            case GridButton:
                return "android.resource://org.jellyfin.androidtv/" + gridButton.getImageIndex();
            case SeriesTimer:
                return "android.resource://org.jellyfin.androidtv/" + R.drawable.seriestimer;
            case SearchHint:
                return Utils.isNonEmpty(searchHint.getPrimaryImageTag()) ? ImageUtils.getImageUrl(searchHint.getItemId(), ImageType.Primary, searchHint.getPrimaryImageTag(), TvApp.getApplication().getApiClient()) :
                        Utils.isNonEmpty(searchHint.getThumbImageItemId()) ? ImageUtils.getImageUrl(searchHint.getThumbImageItemId(), ImageType.Thumb, searchHint.getThumbImageTag(), TvApp.getApplication().getApiClient()) : null;
        }
        return null;
    }

    public boolean isFavorite() {
        switch (type) {

            case BaseItem:
            case LiveTvRecording:
            case LiveTvProgram:
                return baseItem.getUserData() != null && baseItem.getUserData().getIsFavorite();
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case GridButton:
                break;
        }

        return false;
    }

    public boolean isPlayed() {
        switch (type) {
            case BaseItem:
            case LiveTvRecording:
            case LiveTvProgram:
                return baseItem.getUserData() != null && baseItem.getUserData().getPlayed();
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case GridButton:
                break;

        }

        return false;
    }

    public String getCardName() {
        switch (type) {
            case BaseItem:
                if ("Audio".equals(baseItem.getType())) return baseItem.getAlbumArtist() != null ? baseItem.getAlbumArtist() : baseItem.getAlbum() != null ? baseItem.getAlbum() : "<Unknown>";
            default:
                return getFullName();
        }
    }

    public String getFullName() {
        switch (type) {

            case BaseItem:
            case LiveTvProgram:
            case LiveTvRecording:
                return BaseItemUtils.getFullName(baseItem);
            case Person:
                return person.getName();
            case Chapter:
                return chapterInfo.getName();
            case Server:
                return serverInfo.getName();
            case User:
                return user.getName();
            case LiveTvChannel:
                return channelInfo.getName();
            case GridButton:
                return gridButton.getText();
            case SeriesTimer:
                return seriesTimerInfo.getName();
            case SearchHint:
                return (searchHint.getSeries() != null ? searchHint.getSeries() + " - " : "") + searchHint.getName();
        }

        return TvApp.getApplication().getString(R.string.lbl_bracket_unknown);
    }

    public String getName() {
        switch (type) {

            case BaseItem:
            case LiveTvRecording:
            case LiveTvProgram:
                return "Audio".equals(baseItem.getType())? getFullName() : baseItem.getName();
            case Person:
                return person.getName();
            case Server:
                return serverInfo.getName();
            case User:
                return user.getName();
            case Chapter:
                return chapterInfo.getName();
            case SearchHint:
                return searchHint.getName();
            case LiveTvChannel:
                return channelInfo.getName();
            case GridButton:
                return gridButton.getText();
            case SeriesTimer:
                return seriesTimerInfo.getName();
        }

        return TvApp.getApplication().getString(R.string.lbl_bracket_unknown);
    }

    public String getItemId() {
        switch (type) {

            case BaseItem:
            case LiveTvProgram:
            case LiveTvRecording:
                return baseItem.getId();
            case Person:
                return person.getId();
            case Chapter:
                return chapterInfo.getItemId();
            case Server:
                return serverInfo.getId();
            case User:
                return user.getId();
            case LiveTvChannel:
                return channelInfo.getId();
            case GridButton:
                return null;
            case SearchHint:
                return searchHint.getItemId();
            case SeriesTimer:
                return seriesTimerInfo.getId();
        }

        return null;
    }

    public String getSubText() {
        switch (type) {

            case BaseItem:
                return BaseItemUtils.getSubName(baseItem);
            case Person:
                return person.getRole();
            case Chapter:
                Long pos = chapterInfo.getStartPositionTicks() / 10000;
                return TimeUtils.formatMillis(pos.intValue());
            case Server:
                return serverInfo.getLocalAddress() != null ? serverInfo.getLocalAddress().substring(7) : "";
            case LiveTvChannel:
                return channelInfo.getNumber();
            case LiveTvProgram:
                return baseItem.getEpisodeTitle() != null ? baseItem.getEpisodeTitle() : baseItem.getChannelName();
            case LiveTvRecording:
                return (baseItem.getChannelName() != null ? baseItem.getChannelName() + " - " : "") + (baseItem.getEpisodeTitle() != null ? baseItem.getEpisodeTitle() : "") + " " +
                        new SimpleDateFormat("d MMM").format(TimeUtils.convertToLocalDate(baseItem.getStartDate())) + " " +
                        (android.text.format.DateFormat.getTimeFormat(TvApp.getApplication()).format(TimeUtils.convertToLocalDate(baseItem.getStartDate())) + "-"
                                + android.text.format.DateFormat.getTimeFormat(TvApp.getApplication()).format(TimeUtils.convertToLocalDate(baseItem.getEndDate())));
            case User:
                Date date = user.getLastActivityDate();
                return date != null ? DateUtils.getRelativeTimeSpanString(TimeUtils.convertToLocalDate(date).getTime()).toString() : TvApp.getApplication().getString(R.string.lbl_never);
            case SearchHint:
                return searchHint.getType();
            case SeriesTimer:
                return (Utils.isTrue(seriesTimerInfo.getRecordAnyChannel()) ? "All Channels" : seriesTimerInfo.getChannelName()) + " " + seriesTimerInfo.getDayPattern();
        }

        return "";
    }

    public String getType() {
        switch (type) {

            case BaseItem:
            case LiveTvRecording:
            case LiveTvProgram:
                return baseItem.getType();
            case Person:
                return person.getType();
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                return searchHint.getType();
            case LiveTvChannel:
                return channelInfo.getType();
            case GridButton:
                return "GridButton";
            case SeriesTimer:
                return "SeriesTimer";
        }

        return "";

    }

    public String getSummary() {
        switch (type) {

            case BaseItem:
            case LiveTvRecording:
            case LiveTvProgram:
                return baseItem.getOverview();
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case GridButton:
                break;
            case SeriesTimer:
                return BaseItemUtils.getSeriesOverview(seriesTimerInfo);
        }

        return "";
    }

    public long getRuntimeTicks() {
        switch (type) {

            case LiveTvRecording:
            case BaseItem:
                return baseItem.getRunTimeTicks() != null ? baseItem.getRunTimeTicks() : 0;
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case GridButton:
                break;
            case LiveTvProgram:
                return ((baseItem.getStartDate() != null) && (baseItem.getEndDate() != null)) ? (baseItem.getEndDate().getTime() - (baseItem.getStartDate().getTime() * 10000)) : 0;
        }

        return 0;
    }

    public int getChildCount() {
        switch (type) {

            case BaseItem:
                return isFolder() && !"MusicArtist".equals(baseItem.getType()) && baseItem.getChildCount() != null ? baseItem.getChildCount() : -1;
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case LiveTvRecording:
                break;
            case GridButton:
                break;
            case LiveTvProgram:
                break;
        }

        return -1;
    }

    public String getChildCountStr() {
        if (baseItem != null && "Playlist".equals(baseItem.getType()) && baseItem.getCumulativeRunTimeTicks() != null) {
            return TimeUtils.formatMillis(baseItem.getCumulativeRunTimeTicks() / 10000);
        } else {
            Integer count = getChildCount();
            return count > 0 ? count.toString() : "";

        }
    }

    public String getBackdropImageUrl() {
        switch (type) {
            case BaseItem:
                return ImageUtils.getBackdropImageUrl(baseItem, TvApp.getApplication().getConnectionManager().GetApiClient(baseItem), true);

        }

        return null;
    }

    public Drawable getBadgeImage() {
        switch (type) {

            case BaseItem:
                if (baseItem.getType().equals("Movie") && baseItem.getCriticRating() != null) {
                    return baseItem.getCriticRating() > 59 ? TvApp.getApplication().getDrawableCompat(R.drawable.fresh) : TvApp.getApplication().getDrawableCompat(R.drawable.rotten);
                } else if (baseItem.getType().equals("Program") && baseItem.getTimerId() != null) {
                    return baseItem.getSeriesTimerId() != null ? TvApp.getApplication().getDrawableCompat(R.drawable.recseries) : TvApp.getApplication().getDrawableCompat(R.drawable.rec);
                }
                break;
            case Person:
                break;
            case Server:
                break;
            case User:
                if (user.getHasPassword()) {
                    return TvApp.getApplication().getDrawableCompat(R.drawable.lock);
                }
                break;
            case LiveTvProgram:
                if (baseItem.getTimerId() != null) {
                    return baseItem.getSeriesTimerId() != null ? TvApp.getApplication().getDrawableCompat(R.drawable.recseries) : TvApp.getApplication().getDrawableCompat(R.drawable.rec);
                }
            case Chapter:
                break;
        }

        return TvApp.getApplication().getDrawableCompat(R.drawable.blank10x10);
    }

    public void refresh(final EmptyResponse outerResponse) {
        switch (type) {

            case BaseItem:
                TvApp.getApplication().getApiClient().GetItemAsync(getItemId(), TvApp.getApplication().getCurrentUser().getId(), new Response<BaseItemDto>() {
                    @Override
                    public void onResponse(BaseItemDto response) {
                        baseItem = response;
                        outerResponse.onResponse();
                    }
                });
                break;
            case Person:
                break;
            case Server:
                break;
            case User:
                break;
            case Chapter:
                break;
            case SearchHint:
                break;
            case LiveTvChannel:
                break;
            case LiveTvRecording:
                break;
            case GridButton:
                break;
            case LiveTvProgram:
                break;
        }
    }

    public SelectAction getSelectAction() {
        return selectAction;
    }

    public boolean isStaticHeight() {
        return staticHeight;
    }
    public boolean isPlaying() { return isPlaying; }
    public void setIsPlaying(boolean value) { isPlaying = value; }

    public enum ItemType {
        BaseItem,
        Person,
        Server, User, Chapter, SearchHint, LiveTvChannel, LiveTvRecording, GridButton, SeriesTimer, LiveTvProgram
    }

    public enum SelectAction {
        ShowDetails,
        Play
    }
}

