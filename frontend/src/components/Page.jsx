function Page ({ children }) {
  return (
    <div className="page">
      {children}
    </div>
  )
}

Page.Header = function PageHeader ({ title, subtitle }) {
  return (
    <div className="page__header">
      <h1 className="page__title">{title}</h1>
      <p className="page__subtitle">{subtitle}</p>
    </div>
  )
}

Page.Section = function PageSection ({ children }) {
  return (
    <div className="page__section">
      {children}
    </div>
  )
}

export default Page

