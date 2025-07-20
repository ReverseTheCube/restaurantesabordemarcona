document.addEventListener('DOMContentLoaded', function() {
    // Elementos del DOM
    const form = document.getElementById('incidenciaForm');
    const btnGuardar = document.getElementById('btnGuardar');
    const btnCancelar = document.getElementById('btnCancelar');
    const tablaIncidencias = document.getElementById('tablaIncidencias').querySelector('tbody');
    
    // Variables de estado
    let editando = false;
    let incidenciaEditando = null;
    
    // Cargar incidencias al iniciar
    cargarIncidencias();
    
    // Evento para guardar incidencia
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const titulo = document.getElementById('titulo').value;
        const descripcion = document.getElementById('descripcion').value;
        const prioridad = document.getElementById('prioridad').value;
        const estado = document.getElementById('estado').value;
        
        if (editando) {
            // Actualizar incidencia existente
            incidenciaEditando.titulo = titulo;
            incidenciaEditando.descripcion = descripcion;
            incidenciaEditando.prioridad = prioridad;
            incidenciaEditando.estado = estado;
            
            actualizarIncidencia(incidenciaEditando);
        } else {
            // Crear nueva incidencia
            const nuevaIncidencia = {
                id: Date.now(), // Usamos el timestamp como ID
                titulo,
                descripcion,
                prioridad,
                estado,
                fechaCreacion: new Date().toISOString()
            };
            
            guardarIncidencia(nuevaIncidencia);
        }
        
        // Limpiar formulario
        form.reset();
        document.getElementById('incidenciaId').value = '';
        btnCancelar.style.display = 'none';
        editando = false;
        incidenciaEditando = null;
        btnGuardar.textContent = 'Guardar Incidencia';
    });
    
    // Evento para cancelar edición
    btnCancelar.addEventListener('click', function() {
        form.reset();
        document.getElementById('incidenciaId').value = '';
        btnCancelar.style.display = 'none';
        editando = false;
        incidenciaEditando = null;
        btnGuardar.textContent = 'Guardar Incidencia';
    });
    
    // Función para guardar incidencia en LocalStorage
    function guardarIncidencia(incidencia) {
        let incidencias = obtenerIncidencias();
        incidencias.push(incidencia);
        localStorage.setItem('incidencias', JSON.stringify(incidencias));
        cargarIncidencias();
    }
    
    // Función para actualizar incidencia en LocalStorage
    function actualizarIncidencia(incidencia) {
        let incidencias = obtenerIncidencias();
        incidencias = incidencias.map(item => item.id === incidencia.id ? incidencia : item);
        localStorage.setItem('incidencias', JSON.stringify(incidencias));
        cargarIncidencias();
    }
    
    // Función para eliminar incidencia
    function eliminarIncidencia(id) {
        if (confirm('¿Estás seguro de que quieres eliminar esta incidencia?')) {
            let incidencias = obtenerIncidencias();
            incidencias = incidencias.filter(item => item.id !== id);
            localStorage.setItem('incidencias', JSON.stringify(incidencias));
            cargarIncidencias();
        }
    }
    
    // Función para cargar incidencias en la tabla
    function cargarIncidencias() {
        const incidencias = obtenerIncidencias();
        tablaIncidencias.innerHTML = '';
        
        if (incidencias.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="7">No hay incidencias registradas</td>';
            tablaIncidencias.appendChild(row);
            return;
        }
        
        // Ordenar por fecha de creación (más recientes primero)
        incidencias.sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));
        
        incidencias.forEach(incidencia => {
            const row = document.createElement('tr');
            
            const fecha = new Date(incidencia.fechaCreacion);
            const fechaFormateada = `${fecha.getDate()}/${fecha.getMonth() + 1}/${fecha.getFullYear()} ${fecha.getHours()}:${fecha.getMinutes().toString().padStart(2, '0')}`;
            
            row.innerHTML = `
                <td>${incidencia.id}</td>
                <td>${incidencia.titulo}</td>
                <td>${incidencia.descripcion}</td>
                <td>${incidencia.prioridad}</td>
                <td>${incidencia.estado}</td>
                <td>${fechaFormateada}</td>
                <td class="actions">
                    <button onclick="editarIncidencia(${incidencia.id})" class="btn-edit">Editar</button>
                    <button onclick="eliminarIncidencia(${incidencia.id})" class="btn-danger">Eliminar</button>
                </td>
            `;
            
            tablaIncidencias.appendChild(row);
        });
    }
    
    // Función para obtener todas las incidencias
    function obtenerIncidencias() {
        const incidencias = localStorage.getItem('incidencias');
        return incidencias ? JSON.parse(incidencias) : [];
    }
    
    // Función para editar incidencia (global para que funcione en los botones)
    window.editarIncidencia = function(id) {
        const incidencias = obtenerIncidencias();
        const incidencia = incidencias.find(item => item.id === id);
        
        if (incidencia) {
            document.getElementById('incidenciaId').value = incidencia.id;
            document.getElementById('titulo').value = incidencia.titulo;
            document.getElementById('descripcion').value = incidencia.descripcion;
            document.getElementById('prioridad').value = incidencia.prioridad;
            document.getElementById('estado').value = incidencia.estado;
            
            editando = true;
            incidenciaEditando = incidencia;
            btnGuardar.textContent = 'Actualizar Incidencia';
            btnCancelar.style.display = 'inline-block';
            
            // Scroll al formulario
            document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
        }
    };
    
    // Hacer la función eliminar global
    window.eliminarIncidencia = eliminarIncidencia;
});