export default function ErrorMessage({ message, onRetry }) {
  return (
    <div className="error-box" role="alert">
      <p>{message}</p>
      {onRetry && (
        <button className="secondary-button" onClick={onRetry} type="button">
          Retry
        </button>
      )}
    </div>
  );
}
