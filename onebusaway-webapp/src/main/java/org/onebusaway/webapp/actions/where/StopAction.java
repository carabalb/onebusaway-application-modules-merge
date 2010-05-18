/*
 * Copyright 2008 Brian Ferris
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.onebusaway.webapp.actions.where;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.onebusaway.exceptions.ServiceException;
import org.onebusaway.transit_data.model.AgencyBean;
import org.onebusaway.webapp.impl.WebappArrivalsAndDeparturesModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class StopAction extends AbstractWhereAction implements
    ModelDriven<WebappArrivalsAndDeparturesModel> {

  private static final long serialVersionUID = 1L;

  private List<String> _ids;

  private Set<String> _routes;

  private boolean _needsRedirect = false;

  private WebappArrivalsAndDeparturesModel _model;
  
  @Autowired
  public void setModel(WebappArrivalsAndDeparturesModel model) {
    _model = model;
  }

  /**
   * To give more than one Stop ID, the URL must specify id= more than once.
   */
  public void setId(List<String> ids) {
    // Stop ids needs to be something serializable across the wire
    // XWorks can use its own list implementation for ids
    // We also check for legacy stop ids
    _ids = new ArrayList<String>();
    for (String id : ids) {
      if (!id.contains("_")) {
        id = "1_" + id;
        _needsRedirect = true;
      }
      _ids.add(id);
    }
    _model.setStopIds(_ids);
  }

  public List<String> getId() {
    return _ids;
  }

  /**
   * Supports two styles of URL. It can be comma-seperated, or route= can be
   * given multiple times.
   */
  public void setRoute(List<String> routeLists) {

    _routes = new HashSet<String>();

    for (String routes : routeLists) {
      if (!(routes.length() == 0 || routes.equals("all"))) {
        for (String token : routes.split(",")) {
          if (!token.contains("_"))
            _needsRedirect = true;
          _routes.add(token);
        }
      }
    }

    if (!_routes.isEmpty())
      _model.setRouteFilter(_routes);
  }

  public Set<String> getRoutes() {
    return _routes;
  }

  public void setOrder(String order) {
    if (!_model.setOrderFromString(order))
      addFieldError("order", "unknown order value: " + order);
  }

  @TypeConversion(converter = "org.onebusaway.webapp.actions.where.DateTimeConverter")
  public void setTime(Date time) {
    _model.setTargetTime(time);
  }

  public void setMinutesBefore(int minutesBefore) {
    _model.setMinutesBefore(minutesBefore);
  }

  public void setMinutesAfter(int minutesAfter) {
    _model.setMinutesAfter(minutesAfter);
  }

  @Override
  public WebappArrivalsAndDeparturesModel getModel() {
    return _model;
  }

  @Override
  @Actions( {
      @Action(value = "/where/standard/stop"),
      @Action(value = "/where/iphone/stop"),
      @Action(value = "/where/text/stop")})
  public String execute() throws ServiceException {

    if (_ids == null || _ids.isEmpty())
      return INPUT;

    if (_needsRedirect)
      return "redirect";

    _model.process();

    return SUCCESS;
  }

  public boolean testAgenciesWithDisclaimers(List<AgencyBean> agencies) {
    for (AgencyBean agency : agencies) {
      String disclaimer = agency.getDisclaimer();
      if (disclaimer != null && disclaimer.length() > 0)
        return true;
    }
    return false;
  }

  /**
   * Build URL of stops and routes for the Refined Search page.
   */
  public String getStopAndRouteIdsAsUrlParams() {
    StringBuilder sb = new StringBuilder();
    for (String id : _ids) {
      sb.append("id=" + id + "&");
    }
    if (_routes != null) {
      for (String route : _routes) {
        sb.append("route=" + route + "&");
      }
    }
    sb.setLength(sb.length() - 1); // trim the final "&"
    return sb.toString();
  }
}
