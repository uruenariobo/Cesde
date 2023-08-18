import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './curso.reducer';

export const CursoDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cursoEntity = useAppSelector(state => state.curso.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cursoDetailsHeading">Curso</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{cursoEntity.id}</dd>
          <dt>
            <span id="nombre">Nombre</span>
          </dt>
          <dd>{cursoEntity.nombre}</dd>
          <dt>
            <span id="descripcion">Descripcion</span>
          </dt>
          <dd>{cursoEntity.descripcion}</dd>
          <dt>
            <span id="duracion">Duracion</span>
          </dt>
          <dd>{cursoEntity.duracion}</dd>
          <dt>
            <span id="precio">Precio</span>
          </dt>
          <dd>{cursoEntity.precio}</dd>
          <dt>
            <span id="fechaInicio">Fecha Inicio</span>
          </dt>
          <dd>{cursoEntity.fechaInicio ? <TextFormat value={cursoEntity.fechaInicio} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Docente</dt>
          <dd>{cursoEntity.docente ? cursoEntity.docente.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/curso" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Volver</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/curso/${cursoEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Editar</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CursoDetail;
