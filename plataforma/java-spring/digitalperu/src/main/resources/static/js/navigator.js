// Función para colapsar/expandir el sidebar
function toggleSidebar() {
    const sidebar = document.querySelector('.col-md-2.bg-dark');
    const mainContent = document.querySelector('.col-md-10, .col-md-11');
    
    if (!sidebar) return;
    
    sidebar.classList.toggle('collapsed');
    
    if (sidebar.classList.contains('collapsed')) {
        // Sidebar colapsado - expandir contenido principal
        if (mainContent) {
            mainContent.classList.remove('col-md-10');
            mainContent.classList.add('col-md-11');
        }
        // Guardar estado colapsado
        localStorage.setItem('sidebarCollapsed', 'true');
    } else {
        // Sidebar expandido - contenido principal normal
        if (mainContent) {
            mainContent.classList.remove('col-md-11');
            mainContent.classList.add('col-md-10');
        }
        // Guardar estado expandido
        localStorage.setItem('sidebarCollapsed', 'false');
    }
}

// Función para marcar el link activo según la URL actual
function setActiveLink() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link-custom');
    
    navLinks.forEach(link => {
        // Remover clase active de todos los links
        link.classList.remove('active');
        
        // Obtener el identificador de la página del atributo data-page
        const pageName = link.getAttribute('data-page');
        
        // Si la URL actual contiene el nombre de la página, marcarla como activa
        if (pageName && currentPath.includes(pageName)) {
            link.classList.add('active');
        }
    });
}

// Función para aplicar el estado guardado del sidebar INMEDIATAMENTE
function applySidebarState() {
    const savedState = localStorage.getItem('sidebarCollapsed');
    const sidebarCol = document.querySelector('.col-md-2.bg-dark');
    const mainContent = document.querySelector('.col-md-10, .col-md-11');
    
    if (savedState === 'true' && sidebarCol) {
        // Aplicar estado colapsado ANTES de que se renderice
        sidebarCol.classList.add('sidebar', 'collapsed');
        if (mainContent) {
            mainContent.classList.remove('col-md-10');
            mainContent.classList.add('col-md-11');
        }
    } else if (sidebarCol) {
        sidebarCol.classList.add('sidebar');
    }
}

// Ejecutar INMEDIATAMENTE antes de DOMContentLoaded para evitar parpadeo
applySidebarState();

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    // Aplicar estado guardado nuevamente por si acaso
    applySidebarState();
    
    // Marcar el link activo según la URL
    setActiveLink();
    
    // Agregar efecto de hover mejorado (opcional)
    const navLinks = document.querySelectorAll('.nav-link-custom');
    navLinks.forEach(link => {
        link.addEventListener('mouseenter', function() {
            this.style.transition = 'all 0.2s ease';
        });
    });
});