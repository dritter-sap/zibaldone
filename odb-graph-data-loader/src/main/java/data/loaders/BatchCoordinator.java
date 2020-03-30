package data.loaders;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import data.MemoryUtils;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BatchCoordinator {
  private static final Logger log = LoggerFactory.getLogger(BatchCoordinator.class);

  private long batchSize;

  public BatchCoordinator(final long batchSize) {
    this.batchSize = batchSize;
  }

  public void begin(final ODatabaseSession session) {
    session.begin();
  }

  public void end(final ODatabaseSession session) {
    session.commit();
  }

  public void iterate(final ODatabaseSession session, final long recordNumber, final ProgressBar pb) {
    if (recordNumber % batchSize == 0) {
      session.commit();
      // log.debug("Record: " + recordNumber);
      pb.stepBy(batchSize);
      if (recordNumber % (batchSize * 100) == 0) {
        pb.setExtraMessage("Mem(used/max)" + MemoryUtils.usedMemoryInMB() + "/" + MemoryUtils.maxMemoryInMB());
      }
      session.begin();
    }
  }

  public void iterateVertices(final ODatabaseSession session, final long recordNumber, final ProgressBar pb,
                      Map<String, OElement> batchLocal, Map<String, ORID> globalContext) {
    if (recordNumber % batchSize == 0) {
      session.commit();
      pb.stepBy(batchSize);
      if (recordNumber % (batchSize * 100) == 0) {
        pb.setExtraMessage("Mem(used/max)" + MemoryUtils.usedMemoryInMB() + "/" + MemoryUtils.maxMemoryInMB());
      }
      for (final Map.Entry<String, OElement> tmp : batchLocal.entrySet()) {
        globalContext.put(tmp.getKey(), tmp.getValue().getIdentity());
      }
      session.begin();
    }
  }
}
