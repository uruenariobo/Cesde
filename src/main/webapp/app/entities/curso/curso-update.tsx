import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IDocente } from 'app/shared/model/docente.model';
import { getEntities as getDocentes } from 'app/entities/docente/docente.reducer';
import { ICurso } from 'app/shared/model/curso.model';
import { getEntity, updateEntity, createEntity, reset } from './curso.reducer';

export const CursoUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const docentes = useAppSelector(state => state.docente.entities);
  const cursoEntity = useAppSelector(state => state.curso.entity);
  const loading = useAppSelector(state => state.curso.loading);
  const updating = useAppSelector(state => state.curso.updating);
  const updateSuccess = useAppSelector(state => state.curso.updateSuccess);

  const handleClose = () => {
    navigate('/curso');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDocentes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.fechaInicio = convertDateTimeToServer(values.fechaInicio);

    const entity = {
      ...cursoEntity,
      ...values,
      docente: docentes.find(it => it.id.toString() === values.docente.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          fechaInicio: displayDefaultDateTime(),
        }
      : {
          ...cursoEntity,
          fechaInicio: convertDateTimeFromServer(cursoEntity.fechaInicio),
          docente: cursoEntity?.docente?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="cesdeApp.curso.home.createOrEditLabel" data-cy="CursoCreateUpdateHeading">
            Crear o editar Curso
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="curso-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Nombre"
                id="curso-nombre"
                name="nombre"
                data-cy="nombre"
                type="text"
                validate={{
                  required: { value: true, message: 'Este campo es obligatorio.' },
                }}
              />
              <ValidatedField label="Descripcion" id="curso-descripcion" name="descripcion" data-cy="descripcion" type="text" />
              <ValidatedField label="Duracion" id="curso-duracion" name="duracion" data-cy="duracion" type="text" />
              <ValidatedField label="Precio" id="curso-precio" name="precio" data-cy="precio" type="text" />
              <ValidatedField
                label="Fecha Inicio"
                id="curso-fechaInicio"
                name="fechaInicio"
                data-cy="fechaInicio"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField id="curso-docente" name="docente" data-cy="docente" label="Docente" type="select">
                <option value="" key="0" />
                {docentes
                  ? docentes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/curso" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Volver</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Guardar
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CursoUpdate;
