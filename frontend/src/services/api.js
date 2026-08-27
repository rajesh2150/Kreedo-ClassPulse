const API_BASE_URL = 'http://localhost:8081/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    let errorMessage = 'Something went wrong.';
    try {
      const errorData = await response.json();
      if (errorData && errorData.message) {
        errorMessage = errorData.message;
      }
    } catch {
      errorMessage = response.statusText || errorMessage;
    }
    throw new Error(errorMessage);
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return null;
}

export const studentApi = {
  getStudents: () => request('/students'),
  createStudent: (data) => request('/students', { method: 'POST', body: JSON.stringify(data) }),
  deleteStudent: (id) => request(`/students/${id}`, { method: 'DELETE' }),
};

export const feedbackApi = {
  getFeedback: () => request('/feedback'),
  getFeedbackByStudent: (studentId) => request(`/students/${studentId}/feedback`),
  createFeedback: (data) => request('/feedback', { method: 'POST', body: JSON.stringify(data) }),
  deleteFeedback: (id) => request(`/feedback/${id}`, { method: 'DELETE' }),
};
