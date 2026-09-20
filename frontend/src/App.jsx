import { useEffect, useState } from 'react'
import { Link, Navigate, NavLink, Outlet, Route, Routes, useNavigate, useParams } from 'react-router-dom'
import { api } from './api'

const emptyContent = { title: '', author: '', contentType: '', category: '', description: '', resourceUrl: '' }

function getUser() {
  try {
    return JSON.parse(localStorage.getItem('dkp_user'))
  } catch {
    return null
  }
}

function useAuth() {
  const [user, setUser] = useState(getUser)

  const saveSession = (response) => {
    localStorage.setItem('dkp_token', response.token)
    localStorage.setItem('dkp_user', JSON.stringify(response))
    setUser(response)
  }

  const logout = () => {
    localStorage.removeItem('dkp_token')
    localStorage.removeItem('dkp_user')
    setUser(null)
  }

  return { user, saveSession, logout }
}

function App() {
  const auth = useAuth()
  return (
    <Routes>
      <Route path="/login" element={auth.user ? <Navigate to="/dashboard" replace /> : <Login onLogin={auth.saveSession} />} />
      <Route path="/register" element={auth.user ? <Navigate to="/dashboard" replace /> : <Register onRegister={auth.saveSession} />} />
      <Route element={<ProtectedLayout user={auth.user} logout={auth.logout} />}>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard user={auth.user} />} />
        <Route path="/content" element={<ContentLibrary />} />
        <Route path="/content/:id" element={<ContentDetails />} />
        <Route path="/admin" element={auth.user?.role === 'ADMIN' ? <Admin /> : <Navigate to="/dashboard" replace />} />
      </Route>
      <Route path="*" element={<Navigate to={auth.user ? '/dashboard' : '/login'} replace />} />
    </Routes>
  )
}

function ProtectedLayout({ user, logout }) {
  if (!user) return <Navigate to="/login" replace />
  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand" to="/dashboard"><span className="brand-mark">KC</span><span>Knowledge Commons</span></Link>
        <nav className="nav-links">
          <NavLink to="/dashboard">Dashboard</NavLink>
          <NavLink to="/content">Content Library</NavLink>
          {user.role === 'ADMIN' && <NavLink to="/admin">Admin</NavLink>}
        </nav>
        <button className="button button-quiet" onClick={logout}>Log out</button>
      </header>
      <main className="main-content"><Outlet /></main>
      <footer className="footer">Digital Knowledge Platform <span>•</span> Review-1 project</footer>
    </div>
  )
}

function PageHeader({ eyebrow, title, children }) {
  return <div className="page-header"><div><p className="eyebrow">{eyebrow}</p><h1>{title}</h1></div>{children}</div>
}

function Notice({ type = 'error', children }) {
  return <div className={`notice notice-${type}`} role="alert">{children}</div>
}

function Login({ onLogin }) {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [state, setState] = useState({ loading: false, error: '' })

  async function submit(event) {
    event.preventDefault()
    if (!form.email || !form.password) return setState({ loading: false, error: 'Enter your email and password to continue.' })
    setState({ loading: true, error: '' })
    try { const response = await api.login(form); onLogin(response); navigate('/dashboard') }
    catch (error) { setState({ loading: false, error: error.message }) }
  }

  return <AuthLayout title="Welcome back" subtitle="Sign in to continue learning.">
    <form className="auth-form" onSubmit={submit}>
      {state.error && <Notice>{state.error}</Notice>}
      <Field label="Email address" type="email" value={form.email} onChange={(value) => setForm({ ...form, email: value })} placeholder="you@college.edu" />
      <Field label="Password" type="password" value={form.password} onChange={(value) => setForm({ ...form, password: value })} placeholder="Enter your password" />
      <button className="button button-primary button-full" disabled={state.loading}>{state.loading ? 'Signing in...' : 'Sign in'}</button>
      <p className="auth-switch">New to Knowledge Commons? <Link to="/register">Create an account</Link></p>
    </form>
  </AuthLayout>
}

function Register({ onRegister }) {
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '', confirmPassword: '' })
  const [state, setState] = useState({ loading: false, error: '' })

  async function submit(event) {
    event.preventDefault()
    if (!form.name || !form.email || !form.password || !form.confirmPassword) return setState({ loading: false, error: 'Complete all fields to create your account.' })
    if (form.password !== form.confirmPassword) return setState({ loading: false, error: 'Passwords do not match.' })
    if (form.password.length < 6) return setState({ loading: false, error: 'Password must be at least 6 characters.' })
    setState({ loading: true, error: '' })
    try { const response = await api.register({ name: form.name, email: form.email, password: form.password }); onRegister(response); navigate('/dashboard') }
    catch (error) { setState({ loading: false, error: error.message }) }
  }

  return <AuthLayout title="Create your account" subtitle="Join your college learning community.">
    <form className="auth-form" onSubmit={submit}>
      {state.error && <Notice>{state.error}</Notice>}
      <Field label="Full name" value={form.name} onChange={(value) => setForm({ ...form, name: value })} placeholder="Your name" />
      <Field label="Email address" type="email" value={form.email} onChange={(value) => setForm({ ...form, email: value })} placeholder="you@college.edu" />
      <Field label="Password" type="password" value={form.password} onChange={(value) => setForm({ ...form, password: value })} placeholder="At least 6 characters" />
      <Field label="Confirm password" type="password" value={form.confirmPassword} onChange={(value) => setForm({ ...form, confirmPassword: value })} placeholder="Repeat your password" />
      <button className="button button-primary button-full" disabled={state.loading}>{state.loading ? 'Creating account...' : 'Create account'}</button>
      <p className="auth-switch">Already registered? <Link to="/login">Sign in</Link></p>
    </form>
  </AuthLayout>
}

function AuthLayout({ title, subtitle, children }) {
  return <div className="auth-page"><div className="auth-aside"><Link className="brand brand-light" to="/login"><span className="brand-mark">KC</span><span>Knowledge Commons</span></Link><div className="aside-copy"><p className="eyebrow">Digital Knowledge Platform</p><h1>Learning gets better when it is shared.</h1><p>Find, explore, and manage the resources that move your studies forward.</p></div><span className="aside-caption">A focused home for academic discovery.</span></div><div className="auth-panel"><div className="auth-card"><p className="eyebrow">Student portal</p><h2>{title}</h2><p className="muted">{subtitle}</p>{children}</div></div></div>
}

function Field({ label, type = 'text', value, onChange, placeholder }) {
  return <label className="field"><span>{label}</span><input required type={type} value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} /></label>
}

function Dashboard({ user }) {
  const [count, setCount] = useState(null)
  useEffect(() => { api.listContent().then((items) => setCount(items.length)).catch(() => setCount(null)) }, [])
  return <div className="content-width"><PageHeader eyebrow="Overview" title={`Good to see you, ${user.name?.split(' ')[0] || 'there'}.`}><Link className="button button-primary" to="/content">Browse library <span>→</span></Link></PageHeader><section className="welcome-banner"><div><p className="eyebrow">Your learning space</p><h2>Make today a day of progress.</h2><p>Explore trusted resources curated for curious minds.</p></div><span className="banner-number">{count ?? '—'}</span><small>available resources</small></section><section className="stats-grid"><div className="stat-card"><span className="stat-label">Available resources</span><strong>{count ?? '—'}</strong><span className="stat-note">Across all categories</span></div><div className="stat-card"><span className="stat-label">Your role</span><strong className="role-value">{user.role || 'STUDENT'}</strong><span className="stat-note">Access level</span></div><div className="stat-card"><span className="stat-label">Learning activity</span><strong>—</strong><span className="stat-note">Usage service not connected</span></div></section><div className="section-heading"><div><p className="eyebrow">Quick access</p><h2>Where would you like to go?</h2></div></div><section className="quick-grid"><Link className="quick-card quick-card-accent" to="/content"><span className="quick-icon">↗</span><h3>Browse Content</h3><p>Explore the academic resource library.</p><span className="card-arrow">Open library →</span></Link><Link className="quick-card" to="/content"><span className="quick-icon">⌕</span><h3>Search Resources</h3><p>Find material by title, author, or category.</p><span className="card-arrow">Start searching →</span></Link><div className="quick-card is-disabled"><span className="quick-icon">◷</span><h3>My Access & Usage</h3><p>Access tracking will appear when the service is available.</p><span className="card-arrow">Coming soon</span></div>{user.role === 'ADMIN' && <Link className="quick-card" to="/admin"><span className="quick-icon">✦</span><h3>Manage Content</h3><p>Add, update, and curate resources.</p><span className="card-arrow">Open admin →</span></Link>}</section></div>
}

function ContentLibrary() {
  const [items, setItems] = useState([])
  const [query, setQuery] = useState('')
  const [state, setState] = useState({ loading: true, error: '' })

  async function load(keyword = '') { setState({ loading: true, error: '' }); try { setItems(keyword ? await api.searchContent(keyword) : await api.listContent()); setState({ loading: false, error: '' }) } catch (error) { setState({ loading: false, error: error.message }) } }
  useEffect(() => { load() }, [])
  function submit(event) { event.preventDefault(); load(query.trim()) }

  return <div className="content-width"><PageHeader eyebrow="Library" title="Find something worth learning."><span className="result-count">{items.length} resources</span></PageHeader><form className="search-bar" onSubmit={submit}><span className="search-symbol">⌕</span><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search by title, author, or category" aria-label="Search content" /><button className="button button-dark">Search</button></form>{state.error && <Notice>{state.error}</Notice>}{state.loading ? <Loading label="Loading resources..." /> : items.length ? <div className="resource-grid">{items.map((item) => <ResourceCard key={item.id} item={item} />)}</div> : <EmptyState query={query} />}</div>
}

function ResourceCard({ item }) { return <article className="resource-card"><div className="resource-top"><span className="resource-type">{item.contentType}</span><span className="resource-category">{item.category}</span></div><h2>{item.title}</h2><p className="resource-author">By {item.author}</p><p className="resource-description">{item.description || 'No description provided for this resource.'}</p><Link className="resource-link" to={`/content/${item.id}`}>View resource <span>→</span></Link></article> }
function EmptyState({ query }) { return <div className="empty-state"><span className="empty-mark">⌕</span><h2>{query ? 'No matching resources' : 'The library is empty'}</h2><p>{query ? 'Try a different title, author, or category.' : 'Resources added by your college will appear here.'}</p></div> }
function Loading({ label }) { return <div className="loading"><span className="spinner" />{label}</div> }

function ContentDetails() {
  const { id } = useParams(); const navigate = useNavigate(); const [item, setItem] = useState(null); const [error, setError] = useState('')
  useEffect(() => { api.getContent(id).then(setItem).catch((reason) => setError(reason.message)) }, [id])
  if (error) return <div className="content-width"><Notice>{error}</Notice><button className="button button-dark" onClick={() => navigate('/content')}>Back to library</button></div>
  if (!item) return <div className="content-width"><Loading label="Loading resource..." /></div>
  return <div className="content-width"><button className="back-link" onClick={() => navigate('/content')}>← Back to library</button><article className="detail-card"><div className="detail-heading"><div><span className="resource-type">{item.contentType}</span><p className="eyebrow">{item.category}</p><h1>{item.title}</h1><p className="resource-author">By {item.author}</p></div><span className="detail-id">RESOURCE #{item.id}</span></div><div className="detail-body"><div><p className="eyebrow">About this resource</p><p className="detail-description">{item.description || 'No description provided.'}</p></div><div className="resource-url"><span className="eyebrow">Resource URL</span><a href={item.resourceUrl} target="_blank" rel="noreferrer">{item.resourceUrl}</a><a className="button button-primary" href={item.resourceUrl} target="_blank" rel="noreferrer">Open resource <span>↗</span></a></div></div></article></div>
}

function Admin() {
  const [items, setItems] = useState([]); const [editing, setEditing] = useState(null); const [notice, setNotice] = useState({ type: '', text: '' }); const [loading, setLoading] = useState(true)
  async function refresh() { setLoading(true); try { setItems(await api.listContent()); setNotice({ type: '', text: '' }) } catch (error) { setNotice({ type: 'error', text: error.message }) } finally { setLoading(false) } }
  useEffect(() => { refresh() }, [])
  async function remove(id) { if (!window.confirm('Delete this resource?')) return; try { await api.deleteContent(id); setNotice({ type: 'success', text: 'Resource deleted successfully.' }); refresh() } catch (error) { setNotice({ type: 'error', text: error.message }) } }
  return <div className="content-width"><PageHeader eyebrow="Administration" title="Content management"><button className="button button-primary" onClick={() => setEditing(emptyContent)}>+ Add content</button></PageHeader>{notice.text && <Notice type={notice.type}>{notice.text}</Notice>}{editing && <ContentForm initial={editing} onCancel={() => setEditing(null)} onSaved={() => { setEditing(null); setNotice({ type: 'success', text: 'Resource saved successfully.' }); refresh() }} />}{loading ? <Loading label="Loading resources..." /> : <div className="admin-table-wrap"><table className="admin-table"><thead><tr><th>Resource</th><th>Author</th><th>Type</th><th>Category</th><th>Actions</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item.title}</strong><small>#{item.id}</small></td><td>{item.author}</td><td><span className="table-tag">{item.contentType}</span></td><td>{item.category}</td><td className="table-actions"><button className="text-button" onClick={() => setEditing(item)}>Edit</button><button className="text-button danger" onClick={() => remove(item.id)}>Delete</button></td></tr>)}</tbody></table></div>}</div>
}

function ContentForm({ initial, onCancel, onSaved }) {
  const [form, setForm] = useState({ ...emptyContent, ...initial }); const [error, setError] = useState(''); const isEdit = Boolean(initial.id)
  async function submit(event) { event.preventDefault(); setError(''); try { if (isEdit) await api.updateContent(initial.id, form); else await api.createContent(form); onSaved() } catch (reason) { setError(reason.message) } }
  function change(key, value) { setForm({ ...form, [key]: value }) }
  return <div className="form-panel"><div className="form-panel-heading"><div><p className="eyebrow">{isEdit ? 'Update resource' : 'New resource'}</p><h2>{isEdit ? 'Edit content' : 'Add content'}</h2></div><button className="close-button" onClick={onCancel} aria-label="Close form">×</button></div>{error && <Notice>{error}</Notice>}<form className="content-form" onSubmit={submit}><Field label="Title" value={form.title} onChange={(value) => change('title', value)} placeholder="Resource title" /><Field label="Author" value={form.author} onChange={(value) => change('author', value)} placeholder="Author or institution" /><div className="form-row"><Field label="Content type" value={form.contentType} onChange={(value) => change('contentType', value)} placeholder="BOOK, VIDEO..." /><Field label="Category" value={form.category} onChange={(value) => change('category', value)} placeholder="PROGRAMMING, SCIENCE..." /></div><label className="field"><span>Description</span><textarea value={form.description} onChange={(event) => change('description', event.target.value)} placeholder="What will students learn?" rows="4" /></label><Field label="Resource URL" type="url" value={form.resourceUrl} onChange={(value) => change('resourceUrl', value)} placeholder="https://..." /><div className="form-actions"><button type="button" className="button button-quiet" onClick={onCancel}>Cancel</button><button className="button button-primary">{isEdit ? 'Save changes' : 'Create resource'}</button></div></form></div>
}

export default App