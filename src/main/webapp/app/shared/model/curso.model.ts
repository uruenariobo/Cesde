import dayjs from 'dayjs';
import { IDocente } from 'app/shared/model/docente.model';

export interface ICurso {
  id?: number;
  nombre?: string;
  descripcion?: string | null;
  duracion?: number | null;
  precio?: number | null;
  fechaInicio?: string | null;
  docente?: IDocente | null;
}

export const defaultValue: Readonly<ICurso> = {};
