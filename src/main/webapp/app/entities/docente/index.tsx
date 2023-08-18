import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Docente from './docente';
import DocenteDetail from './docente-detail';
import DocenteUpdate from './docente-update';
import DocenteDeleteDialog from './docente-delete-dialog';

const DocenteRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Docente />} />
    <Route path="new" element={<DocenteUpdate />} />
    <Route path=":id">
      <Route index element={<DocenteDetail />} />
      <Route path="edit" element={<DocenteUpdate />} />
      <Route path="delete" element={<DocenteDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DocenteRoutes;
