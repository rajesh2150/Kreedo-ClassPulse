export default function FeedbackForm({ students, selectedStudentId, note, onStudentChange, onNoteChange, onSubmit, loading, error }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Log Feedback</h2>
      </div>
      <form onSubmit={onSubmit} className="stack-form">
        <label>
          Student
          <select value={selectedStudentId} onChange={onStudentChange} disabled={loading || students.length === 0}>
            {students.length === 0 ? (
              <option value="">No students yet</option>
            ) : (
              <>
                <option value="">Select a student</option>
                {students.map((student) => (
                  <option key={student.id} value={student.id}>
                    {student.name}
                  </option>
                ))}
              </>
            )}
          </select>
        </label>

        <label>
          Feedback Note
          <textarea
            value={note}
            onChange={onNoteChange}
            placeholder="e.g. Excellent participation in class"
            rows="4"
            disabled={loading}
          />
        </label>

        {error && <div className="inline-error">{error}</div>}

        <button className="primary-button" type="submit" disabled={loading || !selectedStudentId || !note.trim()}>
          {loading ? 'Adding feedback...' : 'Add Feedback'}
        </button>
      </form>
    </section>
  );
}
