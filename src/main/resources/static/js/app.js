document.addEventListener('DOMContentLoaded', () => {
	const toggle = document.getElementById('sidebarToggle');
	const sidebar = document.getElementById('sidebar');

	if (toggle && sidebar) {
		toggle.addEventListener('click', () => {
			sidebar.classList.toggle('open');
		});

		document.addEventListener('click', e => {
			if (sidebar.classList.contains('open') &&
				!sidebar.contains(e.target) &&
				!toggle.contains(e.target)) {
				sidebar.classList.remove('open');
			}
		});
	}

	document.querySelectorAll('.alert').forEach(alert => {
		setTimeout(() => {
			const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
			if (bsAlert) bsAlert.close();
		}, 5000);
	});

	const calendarWeekOffset = window.__weekOffset ?? 0;

	document.querySelectorAll('[data-week-nav]').forEach(btn => {
		btn.addEventListener('click', () => {
			const delta = parseInt(btn.dataset.weekNav);
			window.location.href = `/calendar?week=${calendarWeekOffset + delta}`;
		});
	});

	document.querySelectorAll('[data-confirm]').forEach(el => {
		el.addEventListener('click', e => {
			if (!confirm(el.dataset.confirm)) {
				e.preventDefault();
			}
		});
	});

	document.querySelectorAll('.notif-item.unread').forEach(item => {
		item.addEventListener('click', () => {
			const uid = item.dataset.uid;
			if (!uid) return;
			fetch(`/notifications/${uid}/seen`, { method: 'POST',
				headers: { 'X-XSRF-TOKEN': getCsrfToken() }
			}).then(() => {
				item.classList.remove('unread');
				item.querySelector('.notif-dot')?.classList.add('read');
			});
		});
	});

	function getCsrfToken() {
		const meta = document.querySelector('meta[name="_csrf"]');
		return meta ? meta.content : '';
	}
});
