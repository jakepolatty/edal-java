/*******************************************************************************
 * Copyright (c) 2013 The University of Reading
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Reading, nor the names of the
 *    authors or contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package uk.ac.rdg.resc.edal.dataset;

import java.io.IOException;
import java.util.Set;

import org.joda.time.DateTime;

import uk.ac.rdg.resc.edal.exceptions.DataReadingException;
import uk.ac.rdg.resc.edal.feature.GridFeature;
import uk.ac.rdg.resc.edal.feature.MapFeature;
import uk.ac.rdg.resc.edal.feature.PointSeriesFeature;
import uk.ac.rdg.resc.edal.feature.ProfileFeature;
import uk.ac.rdg.resc.edal.grid.HorizontalGrid;
import uk.ac.rdg.resc.edal.grid.TimeAxis;
import uk.ac.rdg.resc.edal.grid.VerticalAxis;
import uk.ac.rdg.resc.edal.position.HorizontalPosition;
import uk.ac.rdg.resc.edal.position.VerticalPosition;

/**
 * Interface for reading gridded data and associated metadata.
 * 
 * @author Jon
 * @author Guy
 */
public interface GridDataset extends Dataset {
    /*
     * TODO perhaps this should be the signature for readFeature in GridDatasets?
     */
    // public Feature<GeoPosition> readFeature(String featureId) throws IOException;
    
    /**
     * Extracts a {@link GridFeature} from this {@link GridDataset}
     * 
     * @param varIds
     *            The variables to extract. If this is <code>null</code>, all
     *            available variables (determined by the result of
     *            {@link GridDataset#getVariableIds()}) will be read
     * @param hGrid
     *            The target {@link HorizontalGrid}
     * @param zPos
     *            The target z-position
     * @param time
     *            The target {@link DateTime}
     * @return The extracted {@link GridFeature}
     * @throws IOException
     *             If the underlying data cannot be read for any reason
     */
    public MapFeature readMapData(Set<String> varIds, HorizontalGrid hGrid, Double zPos,
            DateTime time) throws DataReadingException;

    public PointSeriesFeature readTimeSeriesData(Set<String> varIds, HorizontalPosition hPos,
            VerticalPosition zPos, TimeAxis timeAxis) throws DataReadingException;

    public ProfileFeature readProfileData(Set<String> varIds, HorizontalPosition hPos,
            VerticalAxis zAxis, DateTime time) throws DataReadingException;

    
    public Number readSinglePoint(String variableId, HorizontalPosition position, Double zVal,
            DateTime time) throws DataReadingException;
}
