/*******************************************************************************
 * Copyright (c) 2015 The University of Reading
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

import java.util.Collection;
import java.util.List;

import uk.ac.rdg.resc.edal.exceptions.DataReadingException;
import uk.ac.rdg.resc.edal.exceptions.VariableNotFoundException;
import uk.ac.rdg.resc.edal.feature.DiscreteFeature;
import uk.ac.rdg.resc.edal.feature.Feature;
import uk.ac.rdg.resc.edal.feature.GridFeature;
import uk.ac.rdg.resc.edal.grid.GridCell2D;
import uk.ac.rdg.resc.edal.grid.HorizontalGrid;
import uk.ac.rdg.resc.edal.grid.HorizontalMesh;
import uk.ac.rdg.resc.edal.metadata.HorizontalMesh4dVariableMetadata;
import uk.ac.rdg.resc.edal.position.HorizontalPosition;
import uk.ac.rdg.resc.edal.util.Array1D;
import uk.ac.rdg.resc.edal.util.Array2D;
import uk.ac.rdg.resc.edal.util.GridCoordinates2D;
import uk.ac.rdg.resc.edal.util.ValuesArray1D;
import uk.ac.rdg.resc.edal.util.ValuesArray2D;

/**
 * Partial implementation of a {@link Dataset} where the horizontal layers are
 * based on an unstructured mesh, and the vertical / time dimensions are
 * discrete.
 *
 * @author Guy Griffiths
 */
public abstract class HorizontalMesh4dDataset extends
        DiscreteLayeredDataset<HZTDataSource, HorizontalMesh4dVariableMetadata> {

    public HorizontalMesh4dDataset(String id, Collection<HorizontalMesh4dVariableMetadata> vars) {
        super(id, vars);
    }

    @Override
    public Class<? extends DiscreteFeature<?, ?>> getFeatureType(String variableId) {
        /*
         * Whilst feature reading is not yet supported, getFeatureType needs to
         * return a class for the WMS classes (in edal-wms) to work correctly.
         * 
         * TODO Ideally we should create a new HorizontalMesh4dFeature type
         * which shares a parent with GridFeature, and have the WMS check for
         * that parent feature type. Until feature reading is implemented this
         * is extremely low priority.
         */
        return GridFeature.class;
    }

    @Override
    public Feature<?> readFeature(String featureId) throws DataReadingException,
            VariableNotFoundException {
        throw new UnsupportedOperationException("Feature reading is not yet supported");
    }

    @Override
    protected Array2D<Number> extractHorizontalData(HorizontalMesh4dVariableMetadata metadata,
            int tIndex, int zIndex, HorizontalGrid targetGrid, HZTDataSource dataSource)
            throws DataReadingException {
        Array2D<Number> data = new ValuesArray2D(targetGrid.getYSize(), targetGrid.getXSize());
        HorizontalMesh grid = metadata.getHorizontalDomain();
        for (GridCell2D cell : targetGrid.getDomainObjects()) {
            HorizontalPosition centre = cell.getCentre();
            GridCoordinates2D coordinates = cell.getGridCoordinates();
            int hIndex = grid.findIndexOf(centre);
            data.set(dataSource.read(metadata.getId(), tIndex, zIndex, hIndex), coordinates.getY(),
                    coordinates.getX());
        }
        return data;
    }

    @Override
    protected Array1D<Number> extractProfileData(HorizontalMesh4dVariableMetadata metadata,
            List<Integer> zs, int tIndex, HorizontalPosition hPos, HZTDataSource dataSource)
            throws DataReadingException {
        HorizontalMesh hDomain = metadata.getHorizontalDomain();
        int hIndex = hDomain.findIndexOf(hPos);

        Array1D<Number> data = new ValuesArray1D(zs.size());

        int i = 0;
        for (Integer z : zs) {
            data.set(dataSource.read(metadata.getId(), tIndex, z, hIndex), new int[] { i++ });
        }
        return data;
    }

    @Override
    protected Array1D<Number> extractTimeseriesData(HorizontalMesh4dVariableMetadata metadata,
            List<Integer> ts, int zIndex, HorizontalPosition hPos, HZTDataSource dataSource)
            throws DataReadingException {
        HorizontalMesh hDomain = metadata.getHorizontalDomain();
        int hIndex = hDomain.findIndexOf(hPos);

        Array1D<Number> data = new ValuesArray1D(ts.size());

        int i = 0;
        for (Integer t : ts) {
            data.set(dataSource.read(metadata.getId(), t, zIndex, hIndex), new int[] { i++ });
        }
        return data;
    }

    @Override
    protected Number extractPoint(HorizontalMesh4dVariableMetadata metadata, int t, int z,
            HorizontalPosition hPos, HZTDataSource dataSource) throws DataReadingException {
        HorizontalMesh hGrid = metadata.getHorizontalDomain();
        int hIndex = hGrid.findIndexOf(hPos);
        if (hIndex == -1) {
            return null;
        }

        return dataSource.read(metadata.getId(), t, z, hIndex);
    }
}
