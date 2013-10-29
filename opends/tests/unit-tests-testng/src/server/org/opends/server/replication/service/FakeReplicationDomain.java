/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2008-2010 Sun Microsystems, Inc.
 *      Portions Copyright 2013 ForgeRock AS
 */
package org.opends.server.replication.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opends.server.config.ConfigException;
import org.opends.server.replication.plugin.DomainFakeCfg;
import org.opends.server.replication.protocol.UpdateMsg;
import org.opends.server.types.DN;
import org.opends.server.types.DirectoryException;
import org.opends.server.types.ResultCode;

import static org.opends.messages.ReplicationMessages.*;

/**
 * This class is the minimum implementation of a Concrete ReplicationDomain
 * used to test the Generic Replication Service.
 */
@SuppressWarnings("javadoc")
public class FakeReplicationDomain extends ReplicationDomain
{
  /**
   * A blocking queue that is used to send the UpdateMsg received from the
   * Replication Service.
   */
  private BlockingQueue<UpdateMsg> queue;

  /** A string that will be exported should exportBackend be called. */
  private String exportString;

  /**
   * A StringBuilder that will be used to build a build a new String should the
   * import be called.
   */
  private StringBuilder importString;

  private int exportedEntryCount;

  private long generationID = 1;

  private FakeReplicationDomain(DN baseDN, int serverID,
      SortedSet<String> replicationServers, int window, long heartbeatInterval)
      throws ConfigException
  {
    super(baseDN, serverID, 100);
    DomainFakeCfg fakeCfg = new DomainFakeCfg(baseDN, serverID, replicationServers);
    fakeCfg.setHeartbeatInterval(heartbeatInterval);
    fakeCfg.setChangetimeHeartbeatInterval(500);
    fakeCfg.setWindowSize(window);
    startPublishService(fakeCfg);
    startListenService();
  }

  public FakeReplicationDomain(DN baseDN, int serverID,
      SortedSet<String> replicationServers, int window, long heartbeatInterval,
      BlockingQueue<UpdateMsg> queue) throws ConfigException
  {
    this(baseDN, serverID, replicationServers, window, heartbeatInterval);
    this.queue = queue;
  }

  public FakeReplicationDomain(DN baseDN, int serverID,
      SortedSet<String> replicationServers, long heartbeatInterval,
      String exportString, StringBuilder importString, int exportedEntryCount)
      throws ConfigException
  {
    this(baseDN, serverID, replicationServers, 100, heartbeatInterval);
    this.exportString = exportString;
    this.importString = importString;
    this.exportedEntryCount = exportedEntryCount;
  }

  @Override
  public long countEntries() throws DirectoryException
  {
    return exportedEntryCount;
  }

  @Override
  protected void exportBackend(OutputStream output) throws DirectoryException
  {
    try
    {
      output.write(exportString.getBytes());
      output.flush();
      output.close();
    }
    catch (IOException e)
    {
      throw new DirectoryException(ResultCode.OPERATIONS_ERROR,
          ERR_BACKEND_EXPORT_ENTRY.get("", ""));
    }
  }

  @Override
  public long getGenerationID()
  {
    return generationID;
  }

  @Override
  protected void importBackend(InputStream input) throws DirectoryException
  {
    byte[] buffer = new byte[1000];
    int ret;
    do
    {
      try
      {
        ret = input.read(buffer, 0, 1000);
      } catch (IOException e)
      {
        throw new DirectoryException(
            ResultCode.OPERATIONS_ERROR,
            ERR_BACKEND_EXPORT_ENTRY.get("", ""));
      }
      if (ret>0)
        importString.append(new String(buffer, 0, ret));
    }
    while (ret >= 0);
  }

  @Override
  public boolean processUpdate(UpdateMsg updateMsg, AtomicBoolean shutdown)
  {
    if (queue != null)
      queue.add(updateMsg);
    return true;
  }

  public void setGenerationID(long newGenerationID)
  {
    generationID = newGenerationID;
  }
}
