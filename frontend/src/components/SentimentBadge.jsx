const sentimentStyles = {
  POSITIVE: { label: 'POSITIVE', className: 'badge positive' },
  NEUTRAL: { label: 'NEUTRAL', className: 'badge neutral' },
  NEGATIVE: { label: 'NEGATIVE', className: 'badge negative' },
};

export default function SentimentBadge({ sentiment }) {
  const config = sentimentStyles[sentiment] || sentimentStyles.NEUTRAL;
  return <span className={config.className}>{config.label}</span>;
}
