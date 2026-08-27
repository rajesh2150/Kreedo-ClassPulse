export default function StudentForm({ formData, onChange, onSubmit, loading, error }) {
  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Add Student</h2>
      </div>
      <form onSubmit={onSubmit} className="stack-form">
        <label>
          Student Name
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={onChange}
            placeholder="e.g. John Smith"
            disabled={loading}
          />
        </label>
        {error && <div className="inline-error">{error}</div>}
        <button className="primary-button" type="submit" disabled={loading}>
          {loading ? 'Adding student...' : 'Add Student'}
        </button>
      </form>
    </section>
  );
}
