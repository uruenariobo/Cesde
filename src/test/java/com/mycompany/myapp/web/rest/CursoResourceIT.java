package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static com.mycompany.myapp.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Curso;
import com.mycompany.myapp.repository.CursoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CursoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CursoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURACION = 1;
    private static final Integer UPDATED_DURACION = 2;

    private static final BigDecimal DEFAULT_PRECIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_FECHA_INICIO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FECHA_INICIO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/cursos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCursoMockMvc;

    private Curso curso;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createEntity(EntityManager em) {
        Curso curso = new Curso()
            .nombre(DEFAULT_NOMBRE)
            .descripcion(DEFAULT_DESCRIPCION)
            .duracion(DEFAULT_DURACION)
            .precio(DEFAULT_PRECIO)
            .fechaInicio(DEFAULT_FECHA_INICIO);
        return curso;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Curso createUpdatedEntity(EntityManager em) {
        Curso curso = new Curso()
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .duracion(UPDATED_DURACION)
            .precio(UPDATED_PRECIO)
            .fechaInicio(UPDATED_FECHA_INICIO);
        return curso;
    }

    @BeforeEach
    public void initTest() {
        curso = createEntity(em);
    }

    @Test
    @Transactional
    void createCurso() throws Exception {
        int databaseSizeBeforeCreate = cursoRepository.findAll().size();
        // Create the Curso
        restCursoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(curso)))
            .andExpect(status().isCreated());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeCreate + 1);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testCurso.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testCurso.getDuracion()).isEqualTo(DEFAULT_DURACION);
        assertThat(testCurso.getPrecio()).isEqualByComparingTo(DEFAULT_PRECIO);
        assertThat(testCurso.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
    }

    @Test
    @Transactional
    void createCursoWithExistingId() throws Exception {
        // Create the Curso with an existing ID
        curso.setId(1L);

        int databaseSizeBeforeCreate = cursoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCursoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(curso)))
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = cursoRepository.findAll().size();
        // set the field null
        curso.setNombre(null);

        // Create the Curso, which fails.

        restCursoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(curso)))
            .andExpect(status().isBadRequest());

        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCursos() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        // Get all the cursoList
        restCursoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(curso.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].duracion").value(hasItem(DEFAULT_DURACION)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(sameNumber(DEFAULT_PRECIO))))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(sameInstant(DEFAULT_FECHA_INICIO))));
    }

    @Test
    @Transactional
    void getCurso() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        // Get the curso
        restCursoMockMvc
            .perform(get(ENTITY_API_URL_ID, curso.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(curso.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.duracion").value(DEFAULT_DURACION))
            .andExpect(jsonPath("$.precio").value(sameNumber(DEFAULT_PRECIO)))
            .andExpect(jsonPath("$.fechaInicio").value(sameInstant(DEFAULT_FECHA_INICIO)));
    }

    @Test
    @Transactional
    void getNonExistingCurso() throws Exception {
        // Get the curso
        restCursoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCurso() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();

        // Update the curso
        Curso updatedCurso = cursoRepository.findById(curso.getId()).get();
        // Disconnect from session so that the updates on updatedCurso are not directly saved in db
        em.detach(updatedCurso);
        updatedCurso
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .duracion(UPDATED_DURACION)
            .precio(UPDATED_PRECIO)
            .fechaInicio(UPDATED_FECHA_INICIO);

        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCurso.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCurso))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCurso.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testCurso.getDuracion()).isEqualTo(UPDATED_DURACION);
        assertThat(testCurso.getPrecio()).isEqualByComparingTo(UPDATED_PRECIO);
        assertThat(testCurso.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
    }

    @Test
    @Transactional
    void putNonExistingCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, curso.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(curso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(curso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(curso)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso.nombre(UPDATED_NOMBRE).descripcion(UPDATED_DESCRIPCION).duracion(UPDATED_DURACION);

        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCurso))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCurso.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testCurso.getDuracion()).isEqualTo(UPDATED_DURACION);
        assertThat(testCurso.getPrecio()).isEqualByComparingTo(DEFAULT_PRECIO);
        assertThat(testCurso.getFechaInicio()).isEqualTo(DEFAULT_FECHA_INICIO);
    }

    @Test
    @Transactional
    void fullUpdateCursoWithPatch() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();

        // Update the curso using partial update
        Curso partialUpdatedCurso = new Curso();
        partialUpdatedCurso.setId(curso.getId());

        partialUpdatedCurso
            .nombre(UPDATED_NOMBRE)
            .descripcion(UPDATED_DESCRIPCION)
            .duracion(UPDATED_DURACION)
            .precio(UPDATED_PRECIO)
            .fechaInicio(UPDATED_FECHA_INICIO);

        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCurso.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCurso))
            )
            .andExpect(status().isOk());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
        Curso testCurso = cursoList.get(cursoList.size() - 1);
        assertThat(testCurso.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testCurso.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testCurso.getDuracion()).isEqualTo(UPDATED_DURACION);
        assertThat(testCurso.getPrecio()).isEqualByComparingTo(UPDATED_PRECIO);
        assertThat(testCurso.getFechaInicio()).isEqualTo(UPDATED_FECHA_INICIO);
    }

    @Test
    @Transactional
    void patchNonExistingCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, curso.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(curso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(curso))
            )
            .andExpect(status().isBadRequest());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCurso() throws Exception {
        int databaseSizeBeforeUpdate = cursoRepository.findAll().size();
        curso.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCursoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(curso)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Curso in the database
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCurso() throws Exception {
        // Initialize the database
        cursoRepository.saveAndFlush(curso);

        int databaseSizeBeforeDelete = cursoRepository.findAll().size();

        // Delete the curso
        restCursoMockMvc
            .perform(delete(ENTITY_API_URL_ID, curso.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Curso> cursoList = cursoRepository.findAll();
        assertThat(cursoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
