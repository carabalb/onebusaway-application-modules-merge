package org.onebusaway.transit_data_federation.services.tripplanner;

import java.util.Collection;
import java.util.List;

import org.onebusaway.transit_data_federation.bundle.tasks.transfer_pattern.HubNode;
import org.onebusaway.transit_data_federation.bundle.tasks.transfer_pattern.TransferNode;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;

public interface TransferPatternService {

  /**
   * 
   * @return true is transfer path functionality is enabled
   */
  public boolean isEnabled();

  public Collection<TransferNode> getTransferPatternForStops(
      StopEntry stopFrom, List<StopEntry> stopsTo);

  public Collection<TransferNode> getReverseTransferPatternForStops(
      Iterable<StopEntry> stopsFrom, StopEntry stopTo);

  public Collection<TransferNode> expandNode(HubNode node);
}
