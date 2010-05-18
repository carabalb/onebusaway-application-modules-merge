package org.onebusaway.webapp.gwt.bookmark_edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onebusaway.users.client.model.BookmarkBean;
import org.onebusaway.users.client.model.UserBean;
import org.onebusaway.webapp.gwt.stop_and_route_selection.AbstractStopAndRouteSelectionWidget;
import org.onebusaway.webapp.gwt.where_library.UserContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BookmarkEditWidget extends AbstractStopAndRouteSelectionWidget {

  private static MyUiBinder _uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  TextBox _bookmarkName;

  @UiField
  Anchor _saveBookmarkAnchor;

  private int _bookmarkId;

  public BookmarkEditWidget() {
    initWidget(_uiBinder.createAndBindUi(this));
    initialize();

    _saveBookmarkAnchor.setHref("#save_bookmark");

    loadBookmark();
  }

  @UiHandler("_saveBookmarkAnchor")
  void handleShowCustomViewClick(ClickEvent e) {
    e.preventDefault();

    
    UrlBuilder b = Location.createUrlBuilder();
    
    String path = Location.getPath();
    path = path.replace("bookmark-edit.action", "bookmark-update.action");
    b.setPath(path);
    
    String name = _bookmarkName.getText();
    if( name.length() > 0)
      b.setParameter("name", name);

    String[] stopIds = _stopsById.keySet().toArray(new String[0]);
    b.setParameter("stopId", stopIds);

    boolean allRoutesIncluded = true;
    List<String> routeIds = new ArrayList<String>();
    
    for( Map.Entry<String,Boolean> entry : _routeSelectionById.entrySet() ) {
      if( entry.getValue())
        routeIds.add(entry.getKey());
      else
        allRoutesIncluded = false;
    }
    
    if( ! allRoutesIncluded )
      b.setParameter("routeId", routeIds.toArray(new String[routeIds.size()]));

    Location.assign(b.buildString());
  }

  /****
   *
   ****/

  private void loadBookmark() {
    _bookmarkId = Integer.parseInt(Location.getParameter("id"));
    UserContext context = UserContext.getContext();
    context.getCurrentUser(new UserCallback());
  }

  private void refreshBookmark(BookmarkBean bookmark) {
    List<String> stopIds = bookmark.getStopIds();
    Set<String> routeIds = bookmark.getRouteFilter().getRouteIds();
    setStopsAndRoutes(stopIds, routeIds);
    _bookmarkName.setText(bookmark.getName());
  }

  interface MyUiBinder extends UiBinder<Widget, BookmarkEditWidget> {
  }

  private class UserCallback implements AsyncCallback<UserBean> {

    @Override
    public void onSuccess(UserBean user) {

      if (user == null) {
        System.err.println("bad!");
        return;
      }

      for (BookmarkBean bookmark : user.getBookmarks()) {
        if (bookmark.getId() == _bookmarkId) {
          refreshBookmark(bookmark);
        }
      }
    }

    @Override
    public void onFailure(Throwable arg0) {

    }
  }

}
