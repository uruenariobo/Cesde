import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICurso } from 'app/shared/model/curso.model';
import { getEntities } from './curso.reducer';

export const Curso = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const cursoList = useAppSelector(state => state.curso.entities);
  const loading = useAppSelector(state => state.curso.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="curso-heading" data-cy="CursoHeading">
        Cursos
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refrescar lista
          </Button>
          <Link to="/curso/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Crear nuevo Curso
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {cursoList && cursoList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripcion</th>
                <th>Duracion</th>
                <th>Precio</th>
                <th>Fecha Inicio</th>
                <th>Docente</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cursoList.map((curso, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/curso/${curso.id}`} color="link" size="sm">
                      {curso.id}
                    </Button>
                  </td>
                  <td>{curso.nombre}</td>
                  <td>{curso.descripcion}</td>
                  <td>{curso.duracion}</td>
                  <td>{curso.precio}</td>
                  <td>{curso.fechaInicio ? <TextFormat type="date" value={curso.fechaInicio} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{curso.docente ? <Link to={`/docente/${curso.docente.id}`}>{curso.docente.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/curso/${curso.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">Vista</span>
                      </Button>
                      <Button tag={Link} to={`/curso/${curso.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
                      </Button>
                      <Button tag={Link} to={`/curso/${curso.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Eliminar</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">Ningún Cursos encontrado</div>
        )}
      </div>
    </div>
  );
};

export default Curso;
