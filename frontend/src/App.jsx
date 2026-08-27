import { useEffect, useMemo, useState } from 'react';
import { studentApi, feedbackApi } from './services/api';
import StudentForm from './components/StudentForm';
import FeedbackForm from './components/FeedbackForm';
import LoadingState from './components/LoadingState';
import ErrorMessage from './components/ErrorMessage';
import EmptyState from './components/EmptyState';
import SentimentBadge from './components/SentimentBadge';

const initialStudentForm = { name: '' };

export default function App() {
  const [students, setStudents] = useState([]);
  const [feedbackItems, setFeedbackItems] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState('');
  const [studentName, setStudentName] = useState('');
  const [note, setNote] = useState('');
  const [loadingStudents, setLoadingStudents] = useState(true);
  const [loadingFeedback, setLoadingFeedback] = useState(true);
  const [isAddingStudent, setIsAddingStudent] = useState(false);
  const [isAddingFeedback, setIsAddingFeedback] = useState(false);
  const [isDeletingFeedback, setIsDeletingFeedback] = useState(false);
  const [isDeletingStudent, setIsDeletingStudent] = useState(false);
  const [error, setError] = useState('');
  const [studentError, setStudentError] = useState('');
  const [feedbackError, setFeedbackError] = useState('');

  const summary = useMemo(() => {
    const counts = { POSITIVE: 0, NEUTRAL: 0, NEGATIVE: 0 };
    feedbackItems.forEach((item) => {
      if (counts[item.sentiment] !== undefined) {
        counts[item.sentiment] += 1;
      }
    });
    return {
      students: students.length,
      feedback: feedbackItems.length,
      positive: counts.POSITIVE,
      neutral: counts.NEUTRAL,
      negative: counts.NEGATIVE,
    };
  }, [students, feedbackItems]);

  const loadStudents = async () => {
    try {
      setLoadingStudents(true);
      const data = await studentApi.getStudents();
      setStudents(data || []);
      if (data && data.length > 0 && !selectedStudentId) {
        setSelectedStudentId(String(data[0].id));
      }
    } catch (err) {
      setError('Unable to load students. Is the backend running?');
    } finally {
      setLoadingStudents(false);
    }
  };

  const loadFeedback = async () => {
    try {
      setLoadingFeedback(true);
      const data = await feedbackApi.getFeedback();
      setFeedbackItems(data || []);
    } catch (err) {
      setError('Unable to load feedback. Is the backend running?');
    } finally {
      setLoadingFeedback(false);
    }
  };

  useEffect(() => {
    loadStudents();
    loadFeedback();
  }, []);

  const handleStudentSubmit = async (event) => {
    event.preventDefault();
    const name = studentName.trim();
    if (!name) {
      setStudentError('Student name is required.');
      return;
    }

    try {
      setStudentError('');
      setIsAddingStudent(true);
      const createdStudent = await studentApi.createStudent({ name });
      setStudents((current) => [...current, createdStudent]);
      setSelectedStudentId(String(createdStudent.id));
      setStudentName('');
    } catch (err) {
      setStudentError(err.message || 'Unable to add student.');
    } finally {
      setIsAddingStudent(false);
    }
  };

  const handleFeedbackSubmit = async (event) => {
    event.preventDefault();
    if (!selectedStudentId) {
      setFeedbackError('Please select a student before adding feedback.');
      return;
    }
    if (!note.trim()) {
      setFeedbackError('Feedback note is required.');
      return;
    }

    try {
      setFeedbackError('');
      setIsAddingFeedback(true);
      const createdFeedback = await feedbackApi.createFeedback({
        studentId: Number(selectedStudentId),
        note,
      });
      setFeedbackItems((current) => [createdFeedback, ...current]);
      setNote('');
    } catch (err) {
      setFeedbackError(err.message || 'Unable to add feedback.');
    } finally {
      setIsAddingFeedback(false);
    }
  };

  const handleDeleteFeedback = async (id) => {
    try {
      setIsDeletingFeedback(true);
      await feedbackApi.deleteFeedback(id);
      setFeedbackItems((current) => current.filter((item) => item.id !== id));
    } catch (err) {
      setFeedbackError(err.message || 'Unable to delete feedback.');
    } finally {
      setIsDeletingFeedback(false);
    }
  };

  const handleDeleteStudent = async (id) => {
    try {
      setIsDeletingStudent(true);
      await studentApi.deleteStudent(id);
      const updatedStudents = students.filter((student) => student.id !== id);
      setStudents(updatedStudents);
      setFeedbackItems((current) => current.filter((item) => item.studentId !== id));
      if (updatedStudents.length > 0) {
        setSelectedStudentId(String(updatedStudents[0].id));
      } else {
        setSelectedStudentId('');
      }
    } catch (err) {
      setStudentError(err.message || 'Unable to delete student.');
    } finally {
      setIsDeletingStudent(false);
    }
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Kreedo Teacher workflow</p>
          <h1>ClassPulse</h1>
        </div>
      </header>

      <main className="dashboard">
        <section className="stats-grid">
          <article className="stat-card">
            <span>Students</span>
            <strong>{summary.students}</strong>
          </article>
          <article className="stat-card">
            <span>Total Feedback</span>
            <strong>{summary.feedback}</strong>
          </article>
          <article className="stat-card positive-card">
            <span>Positive</span>
            <strong>{summary.positive}</strong>
          </article>
          <article className="stat-card neutral-card">
            <span>Neutral</span>
            <strong>{summary.neutral}</strong>
          </article>
          <article className="stat-card negative-card">
            <span>Negative</span>
            <strong>{summary.negative}</strong>
          </article>
        </section>

        <section className="forms-grid">
          <StudentForm
            formData={{ name: studentName }}
            onChange={(event) => setStudentName(event.target.value)}
            onSubmit={handleStudentSubmit}
            loading={isAddingStudent}
            error={studentError}
          />

          <FeedbackForm
            students={students}
            selectedStudentId={selectedStudentId}
            note={note}
            onStudentChange={(event) => setSelectedStudentId(event.target.value)}
            onNoteChange={(event) => setNote(event.target.value)}
            onSubmit={handleFeedbackSubmit}
            loading={isAddingFeedback}
            error={feedbackError}
          />
        </section>

        <section className="panel feedback-panel">
          <div className="panel-header">
            <h2>Feedback Overview</h2>
          </div>

          {error && <ErrorMessage message={error} onRetry={() => { loadStudents(); loadFeedback(); }} />}

          {loadingStudents || loadingFeedback ? (
            <div className="loading-stack">
              <LoadingState message="Loading students..." />
              <LoadingState message="Loading feedback..." />
            </div>
          ) : students.length === 0 ? (
            <EmptyState title="No students yet" description="Add your first student to get started." />
          ) : feedbackItems.length === 0 ? (
            <EmptyState title="No feedback yet" description="Add a student and log feedback to get started." />
          ) : (
            <div className="feedback-list">
              {feedbackItems.map((item) => (
                <article key={item.id} className="feedback-card">
                  <div className="feedback-card-header">
                    <div>
                      <h3>{item.studentName}</h3>
                      <span>{new Date(item.timestamp).toLocaleString()}</span>
                    </div>
                    <SentimentBadge sentiment={item.sentiment} />
                  </div>
                  <p>{item.note}</p>
                  <div className="card-actions">
                    <button
                      className="delete-button"
                      type="button"
                      disabled={isDeletingFeedback}
                      onClick={() => handleDeleteFeedback(item.id)}
                    >
                      {isDeletingFeedback ? 'Deleting...' : 'Delete'}
                    </button>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>

        {students.length > 0 && (
          <section className="panel list-panel">
            <div className="panel-header">
              <h2>Students</h2>
            </div>
            <div className="student-list">
              {students.map((student) => (
                <div key={student.id} className="student-row">
                  <div>
                    <strong>{student.name}</strong>
                    <small>{feedbackItems.filter((item) => item.studentId === student.id).length} notes</small>
                  </div>
                  <button
                    type="button"
                    className="delete-button"
                    disabled={isDeletingStudent}
                    onClick={() => handleDeleteStudent(student.id)}
                  >
                    {isDeletingStudent ? 'Deleting...' : 'Delete Student'}
                  </button>
                </div>
              ))}
            </div>
          </section>
        )}
      </main>
    </div>
  );
}
